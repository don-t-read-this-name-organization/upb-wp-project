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
@Table(name = "timetables")
@FieldDefaults(level = PRIVATE)
public class Timetable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id", nullable = false)
  Faculty faculty;

  @Column(name = "filename")
  String filename;

  @Column(name = "file_path", length = 500)
  String filePath;

  @Column(name = "schedule_json", columnDefinition = "LONGTEXT")
  String scheduleJson;

  @Column(name = "semester")
  String semester;

  @Column(name = "year")
  Integer year;

  @CreationTimestamp
  @Column(name = "uploaded_at")
  LocalDateTime uploadedAt;

  @Builder.Default
  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  Boolean active = true;
}
