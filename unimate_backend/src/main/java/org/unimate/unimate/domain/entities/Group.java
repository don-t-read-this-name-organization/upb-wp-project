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
@Table(name = "`groups`",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_groups_name_faculty", columnNames = {"name", "faculty_id"}),
    })
@FieldDefaults(level = PRIVATE)
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false, length = 50)
  String name;

  @Column(nullable = false)
  Integer year;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id", nullable = false)
  @ToString.Exclude
  Faculty faculty;
}
