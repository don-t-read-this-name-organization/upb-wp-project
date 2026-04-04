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
@Table(name = "faculty_links")
@FieldDefaults(level = PRIVATE)
public class FacultyLink {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id")
  @ToString.Exclude
  Faculty faculty;

  @Column(length = 2000)
  String url;

  @Column(length = 100)
  String icon;

  @Column(length = 50)
  String colorClass;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Builder.Default
  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  Boolean active = true;
}
