package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.review.response.ReviewResponse;
import org.unimate.unimate.domain.entities.Professor;
import org.unimate.unimate.domain.entities.Review;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.AlreadyExistsException;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.ProfessorRepository;
import org.unimate.unimate.repository.ReviewRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.ReviewService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {

  ReviewRepository reviewRepository;
  ProfessorRepository professorRepository;
  UserRepository userRepository;

  @Override
  @Transactional
  public ReviewResponse createReview(Integer professorId, String comment, int rating, Integer userId) {
    if (rating < 1 || rating > 5) {
      throw new ValidationException("Rating must be between 1 and 5");
    }

    Professor professor = professorRepository.findByIdAndActiveTrue(professorId)
        .orElseThrow(() -> new NotFoundException("Professor", professorId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User", userId));

    Optional<Review> existingOpt = reviewRepository.findByUserIdAndProfessorId(userId, professorId);
    if (existingOpt.isPresent()) {
      Review existing = existingOpt.get();
      if (Boolean.TRUE.equals(existing.getActive())) {
        throw new AlreadyExistsException("Review", "userId=" + userId + ", professorId=" + professorId);
      }
      existing.setActive(true);
      existing.setComment(comment);
      existing.setRating(rating);
      return ReviewResponse.fromEntity(reviewRepository.save(existing));
    }

    Review review = Review.builder()
        .professor(professor)
        .user(user)
        .comment(comment)
        .rating(rating)
        .active(true)
        .build();

    return ReviewResponse.fromEntity(reviewRepository.save(review));
  }

  @Override
  @Transactional
  public void deleteReview(Integer reviewId, Integer userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException("Review", reviewId));

    if (!review.getUser().getId().equals(userId)) {
      throw new ValidationException("You can only delete your own review");
    }

    review.setActive(false);
    reviewRepository.save(review);
  }

  @Override
  @Transactional
  public void deleteReviewAsAdmin(Integer reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException("Review", reviewId));
    review.setActive(false);
    reviewRepository.save(review);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReviewResponse> getProfessorReviews(Integer professorId) {
    return reviewRepository.findByProfessorIdAndActiveOrderByCreatedAtDesc(professorId, true)
        .stream()
        .map(ReviewResponse::fromEntity)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ReviewResponse> getUserReview(Integer professorId, Integer userId) {
    return reviewRepository.findByUserIdAndProfessorId(userId, professorId)
        .filter(review -> Boolean.TRUE.equals(review.getActive()))
        .map(ReviewResponse::fromEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Review> findById(Integer reviewId) {
    return reviewRepository.findById(reviewId);
  }
}
