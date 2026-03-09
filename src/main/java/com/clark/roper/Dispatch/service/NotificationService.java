package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.entity.Notification;
import com.clark.roper.Dispatch.entity.User;
import com.clark.roper.Dispatch.enums.NotificationType;
import com.clark.roper.Dispatch.exception.ResourceNotFoundException;
import com.clark.roper.Dispatch.repository.NotificationRepository;
import com.clark.roper.Dispatch.repository.UserRepository;
import com.clark.roper.Dispatch.security.JwtService;
import com.clark.roper.Dispatch.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class NotificationService {

  private static final int MAX_PAGE_SIZE = 100;

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final JwtService jwtService;

  public NotificationService(NotificationRepository notificationRepository,
      UserRepository userRepository,
      JwtService jwtService) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }


   // Create a notification for a user

  public void createNotification(User user, NotificationType type, String message, Long referenceId) {
    Notification notification = new Notification();
    notification.setUser(user);
    notification.setType(type);
    notification.setMessage(message);
    notification.setReferenceId(referenceId);
    notificationRepository.save(notification);
  }


   //Paginated notifications for the authenticated user.

  public Page<Map<String, Object>> getMyNotifications(String authHeader, int page, int size) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Page<Notification> notifications = notificationRepository
        .findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE)));

    return notifications.map(this::toMap);
  }

  //Get unread notification count.

  public Map<String, Long> getUnreadCount(String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    long count = notificationRepository.countByUserAndReadFalse(user);
    Map<String, Long> result = new LinkedHashMap<>();
    result.put("unreadCount", count);
    return result;
  }


   // Mark a notification as read.

  @Transactional
  public String markAsRead(Long notificationId, String authHeader) {
    String username = JwtUtil.extractUsernameFromAuthHeader(authHeader, jwtService);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

    if (!notification.getUser().getId().equals(user.getId())) {
      throw new com.clark.roper.Dispatch.exception.UnauthorizedException("Not your notification");
    }

    notification.setRead(true);
    notificationRepository.save(notification);
    return "Marked as read";
  }

  private Map<String, Object> toMap(Notification n) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", n.getId());
    map.put("type", n.getType());
    map.put("message", n.getMessage());
    map.put("read", n.isRead());
    map.put("referenceId", n.getReferenceId());
    map.put("createdAt", n.getCreatedAt());
    return map;
  }
}
