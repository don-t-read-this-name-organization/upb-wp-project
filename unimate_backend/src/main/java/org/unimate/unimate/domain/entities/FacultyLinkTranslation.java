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
@Table(name = "faculty_link_translations",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_faculty_link_translations_link_lang", columnNames = {"faculty_link_id", "language"}),
    })
@FieldDefaults(level = PRIVATE)
public class FacultyLinkTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_link_id")
    @ToString.Exclude
    FacultyLink facultyLink;

    @Column(nullable = false, length = 10)
    String language;

    @Column(nullable = false)
    String title;

    @Column(length = 500)
    String description;
}
