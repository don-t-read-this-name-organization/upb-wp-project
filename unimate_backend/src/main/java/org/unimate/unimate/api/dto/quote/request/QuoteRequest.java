package org.unimate.unimate.api.dto.quote.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class QuoteRequest {
    @NotNull
    @Size(min = 6, max = 256)
    String text;
    @NotNull
    @Size(min = 6, max = 256)
    String author;
}
