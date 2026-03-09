package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.entity.UserBlock;
import com.clark.roper.Dispatch.exception.BadRequestException;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.UserBlockRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BlockService {

  private final UserBlockRepository userBlockRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final NotificationService notificationService;



  @Transactional
  public String blockUser(Long targetUserId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User blocker = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    User blocked = userRepository.findById(targetUserId)
        .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

    if (blocker.getId().equals(blocked.getId())) {
      throw new BadRequestException("Cannot block yourself");
    }
    if (userBlockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
      throw new BadRequestException("User already blocked");
    }

    UserBlock block = new UserBlock();
    block.setBlocker(blocker);
    block.setBlocked(blocked);
    userBlockRepository.save(block);

    return "User blocked successfully";
  }

  @Transactional
  public String unblockUser(Long targetUserId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User blocker = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    User blocked = userRepository.findById(targetUserId)
        .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

    UserBlock block = userBlockRepository.findByBlockerAndBlocked(blocker, blocked)
        .orElseThrow(() -> new ResourceNotFoundException("Block not found"));

    userBlockRepository.delete(block);
    return "User unblocked successfully";
  }

  public List<Long> getBlockedUserIds(String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User blocker = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return userBlockRepository.findByBlocker(blocker).stream()
        .map(b -> b.getBlocked().getId())
        .collect(Collectors.toList());
  }

  public boolean isBlockedEitherWay(User user1, User user2) {
    return userBlockRepository.isBlockedEitherWay(user1, user2);
  }
}
