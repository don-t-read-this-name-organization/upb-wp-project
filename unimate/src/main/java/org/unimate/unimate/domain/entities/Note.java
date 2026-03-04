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
@Table(name = "notes")
@FieldDefaults(level = PRIVATE)
public class Note {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column
  String title;

  @Lob
  @Column
  String content;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column
  @Builder.Default
  Boolean active = true;
}
