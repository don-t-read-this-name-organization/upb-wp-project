package org.unimate.unimate.service;

import org.unimate.unimate.domain.entities.Notification;

import java.util.List;

public interface NotificationService {
  Notification create(Integer userId, String type, String title, String message, String relatedEntityType, Integer relatedEntityId);

  List<Notification> getUserNotifications(Integer userId);

  Integer getUnreadCount(Integer userId);

  Notification markAsRead(Integer notificationId);

  void markAllAsRead(Integer userId);

  void delete(Integer notificationId);
}
