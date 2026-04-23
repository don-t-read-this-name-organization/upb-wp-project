package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.domain.entities.Notification;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.repository.NotificationRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.NotificationService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public Notification create(Integer userId, String type, String title, String message, String relatedEntityType, Integer relatedEntityId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User", userId));

    Notification notification = Notification.builder()
        .user(user)
        .type(type)
        .title(title)
        .message(message)
        .relatedEntityType(relatedEntityType)
        .relatedEntityId(relatedEntityId)
        .read(false)
        .build();

    return notificationRepository.save(notification);
  }

  @Override
  public List<Notification> getUserNotifications(Integer userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }

  @Override
  public Integer getUnreadCount(Integer userId) {
    return notificationRepository.countUnreadByUserId(userId);
  }

  @Transactional
  @Override
  public Notification markAsRead(Integer notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotFoundException("Notification", notificationId));

    notification.setRead(true);
    return notificationRepository.save(notification);
  }

  @Transactional
  @Override
  public void markAllAsRead(Integer userId) {
    List<Notification> unreadNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .filter(n -> !n.getRead())
        .toList();

    unreadNotifications.forEach(n -> n.setRead(true));
    notificationRepository.saveAll(unreadNotifications);
  }

  @Transactional
  @Override
  public void delete(Integer notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new NotFoundException("Notification", notificationId));

    notification.setActive(false);
    notificationRepository.save(notification);
  }
}
