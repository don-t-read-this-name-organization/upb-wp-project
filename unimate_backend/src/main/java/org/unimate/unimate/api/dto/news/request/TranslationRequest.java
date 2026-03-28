package org.unimate.unimate.api.dto.news.request;

import jakarta.validation.constraints.NotBlank;
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
public class TranslationRequest {
    @NotBlank
    @Size(min = 2, max = 2)
    String language;

    @NotBlank
    @Size(min = 1, max = 255)
    String title;

    @NotBlank
    String body;
}
