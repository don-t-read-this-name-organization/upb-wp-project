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
@Table(name = "tasks")
@FieldDefaults(level = PRIVATE)
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column
  String title;

  @Column
  String status;

  @Column
  Integer priority;

  @Column(name = "kanban_column")
  String kanbanColumn;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column
  @Builder.Default
  Boolean active = true;
}
