package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "schedules")
@FieldDefaults(level = PRIVATE)
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "course_name", nullable = false)
  String courseName;

  @Column(name = "day_of_week", nullable = false, length = 20)
  String dayOfWeek;

  @Column(name = "start_time", nullable = false)
  LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  LocalTime endTime;

  @Column(name = "location", nullable = false, length = 255)
  String location;

  @CreationTimestamp
  @Column(name = "created_at")
  LocalDateTime createdAt;

  @Builder.Default
  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  Boolean active = true;
}
