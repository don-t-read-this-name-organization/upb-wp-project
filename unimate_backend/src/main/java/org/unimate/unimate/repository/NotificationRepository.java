package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.unimate.unimate.domain.entities.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
  @Query("SELECT n FROM Notification n WHERE n.user.id = ?1 AND n.active = true ORDER BY n.createdAt DESC")
  List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

  @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = ?1 AND n.active = true AND n.read = false")
  Integer countUnreadByUserId(Integer userId);
}
