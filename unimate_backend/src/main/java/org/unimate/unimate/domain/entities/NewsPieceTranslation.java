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
@Table(name = "news_translations")
@FieldDefaults(level = PRIVATE)
public class NewsPieceTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    @ToString.Exclude
    NewsPiece newsPiece;

    @Column(name = "language", nullable = false, length = 2)
    String language;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "body", columnDefinition = "LONGTEXT")
    String body;
}
