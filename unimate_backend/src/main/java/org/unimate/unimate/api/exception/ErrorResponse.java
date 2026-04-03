package org.unimate.unimate.api.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
    String code,
    String message,
    Instant timestamp,
    String path,
    Map<String, String> fieldErrors
) {
}
