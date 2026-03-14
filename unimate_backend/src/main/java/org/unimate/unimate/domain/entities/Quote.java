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
@Table(name = "quotes")
@FieldDefaults(level = PRIVATE)
public class Quote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  String text;

  @Column
  String author;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;
}
