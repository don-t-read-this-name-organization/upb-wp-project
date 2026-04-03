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
@Table(name = "password_reset_tokens")
@FieldDefaults(level = PRIVATE)
public class PasswordResetToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  User user;

  @Column(name = "token_hash", nullable = false)
  String tokenHash;

  @Column(name = "expires_at", nullable = false)
  LocalDateTime expiresAt;

  @Column(name = "used", columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean used = false;

  @CreationTimestamp
  @Column(name = "created_at")
  LocalDateTime createdAt;
}
