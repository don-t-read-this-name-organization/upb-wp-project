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
@Table(name = "reviews")
@FieldDefaults(level = PRIVATE)
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "professor_id", nullable = false)
  Professor professor;

  @Column
  Integer rating;

  @Column(length = 1000)
  String comment;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column
  @Builder.Default
  Boolean active = true;
}
