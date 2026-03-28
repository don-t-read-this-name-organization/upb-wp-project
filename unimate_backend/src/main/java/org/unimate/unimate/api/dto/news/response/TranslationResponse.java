package org.unimate.unimate.api.dto.news.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.NewsPieceTranslation;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class TranslationResponse {
    String language;
    String title;
    String body;

    public static TranslationResponse fromEntity(NewsPieceTranslation translation) {
        return TranslationResponse.builder()
                .language(translation.getLanguage())
                .title(translation.getTitle())
                .body(translation.getBody())
                .build();
    }
}
