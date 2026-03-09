package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.*;
import com.clark.roper.Dispatch.entity.*;
import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.exception.UnauthorizedException;
import com.clark.roper.Dispatch.repository.*;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clark.roper.Dispatch.enums.NotificationType;



@Service
@AllArgsConstructor
public class GeneralLettersService {

  private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
      "createdAt", "title", "likeCount", "replyCount");

  private static final int MAX_PAGE_SIZE = 100;

  private final JwtService jwtService;
  private final GeneralLetterRepository generalLetterRepository;
  private final GeneralLetterReplyRepository generalLetterReplyRepository;
  private final GeneralLetterLikeRepository generalLetterLikeRepository;
  private final UserRepository userRepository;
  private final UserProfileRepository userProfileRepository;
  private final TagRepository tagRepository;
  private final LetterTagRepository letterTagRepository;
  private final NotificationService notificationService;
  private final AuditService auditService;
  private final BlockService blockService;

  //Create Letter(profile check, draft support, tags) :

  @Transactional
  public GeneralLetterViewResponse create(GeneralLetterCreateRequest request, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Profile completion check
    if (!userProfileRepository.existsByUser(author)) {
      throw new BadRequestException("Please create your profile before posting letters");
    }

    GeneralLetter letter = new GeneralLetter();
    letter.setAuthor(author);
    letter.setTitle(request.getTitle());
    letter.setContent(request.getContent());

    // Draft support : default to SENT, can be overridden
    if (request.isDraft()) {
      letter.setStatus(SpecificLettersStatusEnum.DRAFT);
    }

    // Scheduled letters
    if (request.getScheduledAt() != null) {
      letter.setScheduledAt(request.getScheduledAt());
      letter.setStatus(SpecificLettersStatusEnum.DRAFT); // stays draft until scheduled time
    }

    generalLetterRepository.save(letter);

    // Tags
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      for (String tagName : request.getTags()) {
        Tag tag = tagRepository.findByName(tagName.toUpperCase())
            .orElseGet(() -> {
              Tag newTag = new Tag();
              newTag.setName(tagName.toUpperCase());
              return tagRepository.save(newTag);
            });
        LetterTag lt = new LetterTag();
        lt.setLetter(letter);
        lt.setTag(tag);
        letterTagRepository.save(lt);
      }
    }

    auditService.log("GENERAL_LETTER_CREATED", username, "GENERAL_LETTER", letter.getId(),
        "Title: " + letter.getTitle());
    return toResponse(letter);
  }

  //List all letters(paginated + sorted):

  public Page<GeneralLetterViewResponse> listAll(int page, int size, String sortBy, String direction) {
    Sort sort = buildSafeSort(sortBy, direction);
    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), sort);
    // Only show SENT letters (not drafts)
    return generalLetterRepository.findByStatus(SpecificLettersStatusEnum.SENT, pageable)
        .map(this::toResponse);
  }

  //Search & Filter:

  public Page<GeneralLetterViewResponse> search(String keyword, String tag, int page, int size) {
    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").descending());

    if (tag != null && !tag.isBlank()) {
      return generalLetterRepository.findByTagName(tag.toUpperCase(), pageable)
          .map(this::toResponse);
    }
    if (keyword != null && !keyword.isBlank()) {
      return generalLetterRepository.searchByKeyword(keyword, pageable)
          .map(this::toResponse);
    }

    return generalLetterRepository.findByStatus(SpecificLettersStatusEnum.SENT, pageable)
        .map(this::toResponse);
  }

  //list my letters:

  public Page<GeneralLetterViewResponse> listMine(String authHeader, int page, int size, String sortBy,
      String direction) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Sort sort = buildSafeSort(sortBy, direction);
    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), sort);
    return generalLetterRepository.findByAuthor(author, pageable).map(this::toResponse);
  }

  //View singlle letter

  public GeneralLetterViewResponse viewById(Long letterId, String authHeader) {
    GeneralLetter letter = generalLetterRepository.findById(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("General letter " + letterId + " not found"));

    //DELETED letters are never viewable
    if (letter.getStatus() == SpecificLettersStatusEnum.DELETED) {
      throw new ResourceNotFoundException("General letter " + letterId + " not found");
    }

    // DRAFT letters are only viewable by the author
    if (letter.getStatus() == SpecificLettersStatusEnum.DRAFT) {
      if (authHeader == null) {
        throw new ResourceNotFoundException("General letter " + letterId + " not found");
      }
      String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
      if (!letter.getAuthor().getUsername().equals(username)) {
        throw new ResourceNotFoundException("General letter " + letterId + " not found");
      }
    }

    return toResponse(letter);
  }

  // Send draft:

  @Transactional
  public String sendDraft(Long letterId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    GeneralLetter letter = generalLetterRepository.findById(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("Letter not found"));

    if (!letter.getAuthor().getId().equals(author.getId())) {
      throw new UnauthorizedException("Not your letter");
    }
    if (letter.getStatus() != SpecificLettersStatusEnum.DRAFT) {
      throw new BadRequestException("Letter is not a draft");
    }

    letter.setStatus(SpecificLettersStatusEnum.SENT);
    letter.setScheduledAt(null);
    generalLetterRepository.save(letter);

    return "Draft sent successfully";
  }

  //Reply:

  @Transactional
  public GeneralLetterReplyResponse reply(Long letterId, GeneralLetterReplyRequest request, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    GeneralLetter letter = generalLetterRepository.findById(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("Letter " + letterId + " not found"));

    //Block check
    if (blockService.isBlockedEitherWay(author, letter.getAuthor())) {
      throw new BadRequestException("Cannot reply — blocked");
    }

    GeneralLetterReply reply = new GeneralLetterReply();
    reply.setLetter(letter);
    reply.setAuthor(author);
    reply.setContent(request.getContent());
    generalLetterReplyRepository.save(reply);

    generalLetterRepository.incrementReplyCount(letterId);

    // Notification
    if (!author.getId().equals(letter.getAuthor().getId())) {
      notificationService.createNotification(letter.getAuthor(), NotificationType.REPLY,
          author.getUsername() + " replied to your letter: " + letter.getTitle(), letter.getId());
    }

    return toReplyResponse(reply);
  }

  //View Replies:

  public Page<GeneralLetterReplyResponse> viewReplies(Long letterId, int page, int size) {
    GeneralLetter letter = generalLetterRepository.findById(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("Letter " + letterId + " not found"));
    Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), Sort.by("createdAt").ascending());
    return generalLetterReplyRepository.findByLetter(letter, pageable).map(this::toReplyResponse);
  }

  // Like / Unlike:

  @Transactional
  public String toggleLike(Long letterId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Acquire pessimistic write lock (SELECT ... FOR UPDATE) to serialize
    // concurrent like/unlike operations on the same letter row.
    GeneralLetter letter = generalLetterRepository.findByIdForUpdate(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("Letter not found"));

    var existingLike = generalLetterLikeRepository.findByLetterAndUser(letter, user);
    if (existingLike.isPresent()) {
      generalLetterLikeRepository.delete(existingLike.get());
      generalLetterRepository.decrementLikeCount(letterId);
      return "Unliked";
    } else {
      GeneralLetterLike like = new GeneralLetterLike();
      like.setLetter(letter);
      like.setUser(user);
      generalLetterLikeRepository.save(like);

      generalLetterRepository.incrementLikeCount(letterId);

      // Notification
      if (!user.getId().equals(letter.getAuthor().getId())) {
        notificationService.createNotification(letter.getAuthor(), NotificationType.LIKE,
            user.getUsername() + " liked your letter: " + letter.getTitle(), letter.getId());
      }
      return "Liked";
    }
  }

  //Soft Delete :

  @Transactional
  public String delete(Long letterId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User author = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    GeneralLetter letter = generalLetterRepository.findById(letterId)
        .orElseThrow(() -> new ResourceNotFoundException("Letter not found"));

    if (!letter.getAuthor().getId().equals(author.getId())) {
      throw new UnauthorizedException("You can only delete your own letters");
    }

    letter.setStatus(SpecificLettersStatusEnum.DELETED);
    generalLetterRepository.save(letter);

    auditService.log("GENERAL_LETTER_DELETED", username, "GENERAL_LETTER", letterId, null);
    return "Letter deleted";
  }

  //Scheduled Letter Publisher

  @Scheduled(fixedRate = 60000) // every minute
  @Transactional
  public void publishScheduledLetters() {
    List<GeneralLetter> scheduled = generalLetterRepository.findDueScheduledLetters(Instant.now());
    for (GeneralLetter letter : scheduled) {
      letter.setStatus(SpecificLettersStatusEnum.SENT);
      letter.setScheduledAt(null);
      generalLetterRepository.save(letter);
    }
  }

  //Counter Reconciliation:

  @Scheduled(fixedRate = 300_000) // every 5 minutes
  @Transactional
  public void reconcileCounters() {
    int likeFixes = generalLetterRepository.reconcileLikeCounts();
    int replyFixes = generalLetterRepository.reconcileReplyCounts();
    if (likeFixes > 0 || replyFixes > 0) {
      org.slf4j.LoggerFactory.getLogger(getClass())
          .warn("Counter reconciliation corrected {} likeCount(s) and {} replyCount(s)",
              likeFixes, replyFixes);
    }
  }

  //Mappers :

  private GeneralLetterViewResponse toResponse(GeneralLetter letter) {
    GeneralLetterViewResponse r = new GeneralLetterViewResponse();
    r.setId(letter.getId());
    r.setAuthorId(letter.getAuthor().getId());
    r.setAuthorUsername(letter.getAuthor().getUsername());
    r.setTitle(letter.getTitle());
    r.setContent(letter.getContent());
    r.setStatus(letter.getStatus().name());
    r.setReplyCount(letter.getReplyCount());
    r.setLikeCount(letter.getLikeCount());
    r.setCreatedAt(letter.getCreatedAt());

    // Tags
    List<LetterTag> letterTags = letterTagRepository.findByLetter(letter);
    Set<String> tagNames = letterTags.stream().map(lt -> lt.getTag().getName()).collect(Collectors.toSet());
    r.setTags(tagNames);

    return r;
  }

  private GeneralLetterReplyResponse toReplyResponse(GeneralLetterReply reply) {
    GeneralLetterReplyResponse r = new GeneralLetterReplyResponse();
    r.setId(reply.getId());
    r.setAuthorId(reply.getAuthor().getId());
    r.setAuthorUsername(reply.getAuthor().getUsername());
    r.setContent(reply.getContent());
    r.setCreatedAt(reply.getCreatedAt());
    return r;
  }

   //Validates sortBy against an allow-list and builds a safe Sort object.

    private Sort buildSafeSort(String sortBy, String direction) {
    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      throw new BadRequestException(
          "Invalid sort field '" + sortBy + "'. Allowed: " + ALLOWED_SORT_FIELDS);
    }
    return direction.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
  }
}
