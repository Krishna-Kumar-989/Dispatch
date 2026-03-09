package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserFollow;
import com.clark.roper.Dispatch.enums.NotificationType;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.UserFollowRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class FollowService {

  private static final int MAX_PAGE_SIZE = 100;

  private final UserFollowRepository userFollowRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final NotificationService notificationService;

  @Transactional
  public String follow(Long targetUserId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User follower = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    User followed = userRepository.findById(targetUserId)
        .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

    if (follower.getId().equals(followed.getId())) {
      throw new BadRequestException("Cannot follow yourself");
    }
    if (userFollowRepository.existsByFollowerAndFollowed(follower, followed)) {
      throw new BadRequestException("Already following this user");
    }

    UserFollow follow = new UserFollow();
    follow.setFollower(follower);
    follow.setFollowed(followed);
    userFollowRepository.save(follow);

    notificationService.createNotification(followed, NotificationType.FOLLOW,
        follower.getUsername() + " started following you", follower.getId());

    return "Following " + followed.getUsername();
  }

  @Transactional
  public String unfollow(Long targetUserId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User follower = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    User followed = userRepository.findById(targetUserId)
        .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

    UserFollow follow = userFollowRepository.findByFollowerAndFollowed(follower, followed)
        .orElseThrow(() -> new ResourceNotFoundException("Not following this user"));

    userFollowRepository.delete(follow);
    return "Unfollowed " + followed.getUsername();
  }

  public Page<Map<String, Object>> getFollowers(Long userId, int page, int size) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return userFollowRepository.findByFollowed(user, PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE)))
        .map(f -> {
          Map<String, Object> map = new LinkedHashMap<>();
          map.put("userId", f.getFollower().getId());
          map.put("username", f.getFollower().getUsername());
          map.put("followedAt", f.getCreatedAt());
          return map;
        });
  }

  public Page<Map<String, Object>> getFollowing(Long userId, int page, int size) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return userFollowRepository.findByFollower(user, PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE)))
        .map(f -> {
          Map<String, Object> map = new LinkedHashMap<>();
          map.put("userId", f.getFollowed().getId());
          map.put("username", f.getFollowed().getUsername());
          map.put("followedAt", f.getCreatedAt());
          return map;
        });
  }

  public Map<String, Long> getCounts(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    Map<String, Long> counts = new LinkedHashMap<>();
    counts.put("followers", userFollowRepository.countByFollowed(user));
    counts.put("following", userFollowRepository.countByFollower(user));
    return counts;
  }
}
