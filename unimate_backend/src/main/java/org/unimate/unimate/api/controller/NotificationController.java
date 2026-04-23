package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.notification.response.NotificationResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.NotificationService;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NotificationController {

  NotificationService notificationService;

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<NotificationResponse> getNotifications(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return notificationService.getUserNotifications(currentUser.getId())
        .stream()
        .map(NotificationResponse::fromEntity)
        .toList();
  }

  @GetMapping("/unread-count")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Integer> getUnreadCount(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return Map.of("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
  }

  @PutMapping("/{id}/read")
  @PreAuthorize("isAuthenticated()")
  public NotificationResponse markAsRead(@PathVariable Integer id) {
    return NotificationResponse.fromEntity(notificationService.markAsRead(id));
  }

  @PutMapping("/mark-all-read")
  @PreAuthorize("isAuthenticated()")
  public void markAllAsRead(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    notificationService.markAllAsRead(currentUser.getId());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public void deleteNotification(@PathVariable Integer id) {
    notificationService.delete(id);
  }
}
