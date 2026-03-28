package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.enums.NoteContentType;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "note_contents")
@FieldDefaults(level = PRIVATE)
public class NoteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    @ToString.Exclude
    Note note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NoteContentType contentType;

    @Column(columnDefinition = "LONGTEXT")
    String content;

    @Column(length = 500)
    String filePath;

    @Column
    Integer sortOrder;
}
