package org.unimate.unimate.api.dto.news.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.NewsPiece;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class NewsPieceResponse {
    Integer id;
    LocalDateTime publishDate;
    List<TranslationResponse> translations;

    public static NewsPieceResponse fromEntity(NewsPiece newsPiece) {
        return NewsPieceResponse.builder()
                .id(newsPiece.getId())
                .publishDate(newsPiece.getPublishDate())
                .translations(newsPiece.getTranslations().stream()
                        .map(TranslationResponse::fromEntity)
                        .toList())
                .build();
    }
}
