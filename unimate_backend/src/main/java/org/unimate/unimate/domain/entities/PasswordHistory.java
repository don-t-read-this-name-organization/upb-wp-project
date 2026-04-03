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
@Table(name = "password_history")
@FieldDefaults(level = PRIVATE)
public class PasswordHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  User user;

  @Column(name = "password_hash", nullable = false)
  String passwordHash;

  @CreationTimestamp
  @Column(name = "changed_at")
  LocalDateTime changedAt;
}
