package org.unimate.unimate.api.dto.review.response;

import lombok.Builder;

import java.util.Map;

@Builder
public record RatingStatsResponse(
    Double average,
    Integer count,
    Map<Integer, Integer> distribution
) {
}
