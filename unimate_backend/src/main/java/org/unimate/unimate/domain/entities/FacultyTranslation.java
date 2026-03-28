package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "faculty_translations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_faculty_translations_faculty_lang", columnNames = {"faculty_id", "language"}),
    })
@FieldDefaults(level = PRIVATE)
public class FacultyTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id", nullable = false)
  @ToString.Exclude
  Faculty faculty;

  @Column(name = "language", nullable = false, length = 2)
  String language;

  @Column(name = "name", nullable = false)
  String name;

  @Column(name = "short_name", length = 50)
  String shortName;
}
