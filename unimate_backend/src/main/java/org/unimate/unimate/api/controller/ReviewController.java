package org.unimate.unimate.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.review.request.ReviewRequest;
import org.unimate.unimate.api.dto.review.response.RatingStatsResponse;
import org.unimate.unimate.api.dto.review.response.ReviewResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.RatingCalculatorService;
import org.unimate.unimate.service.ReviewService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ReviewController {

  ReviewService reviewService;
  RatingCalculatorService ratingCalculatorService;

  @PostMapping
  public ReviewResponse createReview(
      @Valid @RequestBody ReviewRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return reviewService.createReview(
        request.professorId(),
        request.comment(),
        request.rating(),
        currentUser.getId()
    );
  }

  @GetMapping("/professor/{professorId}")
  @PreAuthorize("permitAll()")
  public List<ReviewResponse> getProfessorReviews(@PathVariable Integer professorId) {
    return reviewService.getProfessorReviews(professorId);
  }

  @GetMapping("/professor/{professorId}/rating")
  @PreAuthorize("permitAll()")
  public RatingStatsResponse getProfessorRating(@PathVariable Integer professorId) {
    return ratingCalculatorService.calculateRatingStats(professorId);
  }

  @DeleteMapping("/{id}")
  public void deleteReview(
      @PathVariable Integer id,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    if (currentUser.getRole() == RoleName.ADMIN) {
      reviewService.deleteReviewAsAdmin(id);
      return;
    }
    reviewService.deleteReview(id, currentUser.getId());
  }
}
