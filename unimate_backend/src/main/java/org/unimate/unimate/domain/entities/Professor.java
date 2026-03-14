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
@Table(name = "professors")
@FieldDefaults(level = PRIVATE)
public class Professor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false)
  String name;

  @Column
  String department;

  @Column
  String faculty;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;
}
