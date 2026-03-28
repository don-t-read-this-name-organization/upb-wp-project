package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
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
@Table(name = "news")
@FieldDefaults(level = PRIVATE)
public class NewsPiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "publish_date", nullable = false)
    LocalDateTime publishDate;

    @OneToMany(mappedBy = "newsPiece", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<NewsPieceTranslation> translations = new ArrayList<>();
}
