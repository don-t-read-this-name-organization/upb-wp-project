package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString(exclude = {"user", "subtasks"})
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

  @Column(columnDefinition = "TEXT")
  String description;

  @Column
  String status;

  @Column
  Integer priority;

  @Column(name = "kanban_column")
  String kanbanColumn;

  @Column(name = "deadline")
  LocalDate deadline;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  List<Subtask> subtasks = new ArrayList<>();

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  LocalDateTime updatedAt;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;
}
