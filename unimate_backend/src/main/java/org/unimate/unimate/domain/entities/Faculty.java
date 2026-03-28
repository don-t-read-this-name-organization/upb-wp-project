package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "faculties",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_faculties_name", columnNames = {"name"}),
    })
@FieldDefaults(level = PRIVATE)
public class Faculty {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false)
  String name;

  @Column(length = 50)
  String shortName;

  @Column(length = 2000)
  String website;

  @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  List<FacultyTranslation> translations = new ArrayList<>();
}
