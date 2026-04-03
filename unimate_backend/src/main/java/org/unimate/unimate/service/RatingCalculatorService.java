package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.review.response.RatingStatsResponse;

public interface RatingCalculatorService {
  Double calculateAverageRating(Integer professorId);

  RatingStatsResponse calculateRatingStats(Integer professorId);
}
