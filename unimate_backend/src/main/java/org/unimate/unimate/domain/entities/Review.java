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
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_reviews_user_professor", columnNames = {"user_id", "professor_id"})
    }
)
@FieldDefaults(level = PRIVATE)
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "professor_id", nullable = false)
  Professor professor;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(nullable = false)
  Integer rating;

  @Column(length = 1000)
  String comment;

  @CreationTimestamp
  @Column(name = "created_at")
  LocalDateTime createdAt;

  @Builder.Default
  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  Boolean active = true;
}
