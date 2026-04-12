package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.review.response.ReviewResponse;
import org.unimate.unimate.domain.entities.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
  ReviewResponse createReview(Integer professorId, String comment, int rating, Integer userId);

  void deleteReview(Integer reviewId, Integer userId);

  void deleteReviewAsAdmin(Integer reviewId);

  List<ReviewResponse> getProfessorReviews(Integer professorId);

  Optional<ReviewResponse> getUserReview(Integer professorId, Integer userId);

  Optional<Review> findById(Integer reviewId);
}
