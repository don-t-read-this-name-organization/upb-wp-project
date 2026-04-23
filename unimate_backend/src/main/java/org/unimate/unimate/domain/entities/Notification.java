package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
@FieldDefaults(level = PRIVATE)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(nullable = false)
  String type;

  @Column(nullable = false)
  String title;

  @Column(columnDefinition = "TEXT")
  String message;

  @Column(name = "related_entity_type")
  String relatedEntityType;

  @Column(name = "related_entity_id")
  Integer relatedEntityId;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean read = false;

  @CreationTimestamp
  @Column(name = "created_at")
  LocalDateTime createdAt;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;
}
