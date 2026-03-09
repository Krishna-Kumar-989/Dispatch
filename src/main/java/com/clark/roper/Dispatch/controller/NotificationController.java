package com.clark.roper.Dispatch.controller;

import com.clark.roper.Dispatch.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  public Page<Map<String, Object>> getNotifications(
      @RequestHeader("Authorization") String authHeader,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return notificationService.getMyNotifications(authHeader, page, size);
  }

  @GetMapping("/unread-count")
  public Map<String, Long> getUnreadCount(@RequestHeader("Authorization") String authHeader) {
    return notificationService.getUnreadCount(authHeader);
  }

  @PatchMapping("/{id}/read")
  public String markAsRead(@PathVariable("id") Long id,
      @RequestHeader("Authorization") String authHeader) {
    return notificationService.markAsRead(id, authHeader);
  }
}
