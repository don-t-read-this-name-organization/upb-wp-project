package org.unimate.unimate.api.dto.quote.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Quote;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class QuoteResponse {
    Integer id;
    String text;
    String author;
    Boolean active;

    public static QuoteResponse fromEntity(Quote quote) {
        return QuoteResponse.builder()
                .id(quote.getId())
                .author(quote.getAuthor())
                .text(quote.getText())
                .active(quote.getActive())
                .build();
    }
}
