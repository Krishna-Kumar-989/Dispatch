package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.dto.SpecificLettersReceivedViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersSendRequest;
import com.clark.roper.Dispatch.dto.SpecificLettersSentViewRequestFilter;
import com.clark.roper.Dispatch.dto.SpecificLettersViewResponse;
import com.clark.roper.Dispatch.entity.SpecificLetters;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserConnections;
import com.clark.roper.Dispatch.enums.NotificationType;
import com.clark.roper.Dispatch.enums.SpecificLettersStatusEnum;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.exception.UnauthorizedException;
import com.clark.roper.Dispatch.repository.SpecificLettersRepository;
import com.clark.roper.Dispatch.repository.UserConnectionsRepository;
import com.clark.roper.Dispatch.repository.UserProfileRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SpecificLettersService {

    private static final int MAX_PAGE_SIZE = 100;

    private final JwtService jwtService;
    private final SpecificLettersRepository specificLettersRepository;
    private final UserRepository userRepository;
    private final UserConnectionsRepository userConnectionsRepository;
    private final UserProfileRepository userProfileRepository;
    private final BlockService blockService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    // Send letters (block check, profile check):

    @Transactional
    public String send(SpecificLettersSendRequest specificLettersRequest, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);

        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        // Profile completion check
        if (!userProfileRepository.existsByUser(sender)) {
            throw new BadRequestException("Please create your profile before sending letters");
        }

        Long receiverId = specificLettersRequest.getReceiverId();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver with ID " + receiverId + " not found"));

        // Block check
        if (blockService.isBlockedEitherWay(sender, receiver)) {
            throw new BadRequestException("Cannot send letter — user is blocked");
        }

        // Create and save the letter
        SpecificLetters specificLetters = new SpecificLetters();
        specificLetters.setSender(sender);
        specificLetters.setReceiver(receiver);
        specificLetters.setContent(specificLettersRequest.getContent());
        specificLettersRepository.save(specificLetters);

        // Update or create UserConnections
        Optional<UserConnections> existingConnection = userConnectionsRepository.findConnectionBetweenUsers(sender,
                receiver);

        UserConnections connection;
        if (existingConnection.isPresent()) {
            connection = existingConnection.get();
        } else {
            connection = new UserConnections();
            connection.setUser1(sender);
            connection.setUser2(receiver);
        }

        Instant now = Instant.now();
        if (connection.getUser1().getId().equals(sender.getId())) {
            connection.setLastSentUser1(now);
        } else {
            connection.setLastSentUser2(now);
        }
        connection.setLastInteracted(now);
        userConnectionsRepository.save(connection);

        // Notification
        notificationService.createNotification(receiver, NotificationType.LETTER_RECEIVED,
                sender.getUsername() + " sent you a letter", specificLetters.getId());

        // Audit
        auditService.log("SPECIFIC_LETTER_SENT", username, "SPECIFIC_LETTER",
                specificLetters.getId(), "To: " + receiver.getUsername());

        return "Success";
    }

    //View received letters(PAGINATED):

    public Page<SpecificLettersViewResponse> viewReceived(
            SpecificLettersReceivedViewRequestFilter filterRequest, String authHeader,
            int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SpecificLetters> allLetters = specificLettersRepository.filterLetters(
                filterRequest.getSenderId(),
                receiver.getId(),
                filterRequest.getStatus(),
                filterRequest.getCreatedAtLowerLimit(),
                filterRequest.getCreatedAtHigherLimit());

        // Manual pagination over filtered results
        int start = Math.min(page * size, allLetters.size());
        int end = Math.min(start + size, allLetters.size());
        List<SpecificLetters> pageContent = allLetters.subList(start, end);

        List<SpecificLettersViewResponse> responses = mapToResponseList(pageContent);
        return new org.springframework.data.domain.PageImpl<>(responses,
                PageRequest.of(page, size), allLetters.size());
    }

    //View sent letter(PAGINATED) :

    public Page<SpecificLettersViewResponse> viewSent(
            SpecificLettersSentViewRequestFilter filterRequest, String authHeader,
            int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<SpecificLetters> allLetters = specificLettersRepository.filterLetters(
                sender.getId(),
                filterRequest.getReceiverId(),
                filterRequest.getStatus(),
                filterRequest.getCreatedAtLowerLimit(),
                filterRequest.getCreatedAtHigherLimit());

        int start = Math.min(page * size, allLetters.size());
        int end = Math.min(start + size, allLetters.size());
        List<SpecificLetters> pageContent = allLetters.subList(start, end);

        List<SpecificLettersViewResponse> responses = mapToResponseList(pageContent);
        return new org.springframework.data.domain.PageImpl<>(responses,
                PageRequest.of(page, size), allLetters.size());
    }

    //Set letter status "Read" :

    @Transactional
    public String setStatusRead(Long letterId, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SpecificLetters specificLetter = specificLettersRepository.findById(letterId)
                .orElseThrow(() -> new ResourceNotFoundException("Letter with ID " + letterId + " not found"));

        if (specificLetter.getReceiver().getId().equals(receiver.getId())) {
            specificLetter.setStatus(SpecificLettersStatusEnum.READ);

            User sender = specificLetter.getSender();
            userConnectionsRepository.findConnectionBetweenUsers(receiver, sender)
                    .ifPresent(connection -> {
                        Instant now = Instant.now();
                        if (connection.getUser1().getId().equals(receiver.getId())) {
                            connection.setLastReadUser1(now);
                        } else {
                            connection.setLastReadUser2(now);
                        }
                        connection.setLastInteracted(now);
                        userConnectionsRepository.save(connection);
                    });
        } else {
            throw new UnauthorizedException("You can only mark your own received letters as read");
        }

        return "Success";
    }

    //Soft Delete :

    @Transactional
    public String softDelete(Long letterId, String authHeader) {

        String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SpecificLetters letter = specificLettersRepository.findById(letterId)
                .orElseThrow(() -> new ResourceNotFoundException("Letter not found"));

        // Only sender can delete
        if (!letter.getSender().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own sent letters");
        }

        letter.setStatus(SpecificLettersStatusEnum.DELETED);
        specificLettersRepository.save(letter);

        auditService.log("SPECIFIC_LETTER_DELETED", username, "SPECIFIC_LETTER", letterId, null);
        return "Letter deleted";
    }

    //Helpers:

    private List<SpecificLettersViewResponse> mapToResponseList(List<SpecificLetters> letters) {
        List<SpecificLettersViewResponse> dtoResponseList = new ArrayList<>();
        for (SpecificLetters letter : letters) {
            SpecificLettersViewResponse dto = new SpecificLettersViewResponse();
            dto.setId(letter.getId());
            dto.setSenderId(letter.getSender().getId());
            dto.setReceiverId(letter.getReceiver().getId());
            dto.setStatus(letter.getStatus());
            dto.setContent(letter.getContent());
            dto.setCreatedAt(letter.getCreatedAt());
            dtoResponseList.add(dto);
        }
        return dtoResponseList;
    }
}
