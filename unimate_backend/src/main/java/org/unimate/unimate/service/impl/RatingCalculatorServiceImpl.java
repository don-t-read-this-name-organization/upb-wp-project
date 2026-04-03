package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.review.response.RatingStatsResponse;
import org.unimate.unimate.domain.entities.Review;
import org.unimate.unimate.repository.ReviewRepository;
import org.unimate.unimate.service.RatingCalculatorService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RatingCalculatorServiceImpl implements RatingCalculatorService {

  ReviewRepository reviewRepository;

  @Override
  @Transactional(readOnly = true)
  public Double calculateAverageRating(Integer professorId) {
    List<Review> reviews = reviewRepository.findByProfessorIdAndActiveOrderByCreatedAtDesc(professorId, true);
    if (reviews.isEmpty()) {
      return 0.0;
    }
    double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    return Math.round(avg * 100.0) / 100.0;
  }

  @Override
  @Transactional(readOnly = true)
  public RatingStatsResponse calculateRatingStats(Integer professorId) {
    List<Review> reviews = reviewRepository.findByProfessorIdAndActiveOrderByCreatedAtDesc(professorId, true);

    Map<Integer, Integer> distribution = new LinkedHashMap<>();
    for (int i = 1; i <= 5; i++) {
      distribution.put(i, 0);
    }

    for (Review review : reviews) {
      distribution.computeIfPresent(review.getRating(), (key, value) -> value + 1);
    }

    return RatingStatsResponse.builder()
        .average(calculateAverageRating(professorId))
        .count(reviews.size())
        .distribution(distribution)
        .build();
  }
}
