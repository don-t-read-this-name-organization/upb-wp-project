package org.unimate.unimate.api.dto.review.response;

import lombok.Builder;
import org.unimate.unimate.domain.entities.Review;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(
    Integer id,
    Integer professorId,
    Integer userId,
    String username,
    Integer rating,
    String comment,
    LocalDateTime createdAt,
    Boolean active
) {
  public static ReviewResponse fromEntity(Review review) {
    return ReviewResponse.builder()
        .id(review.getId())
        .professorId(review.getProfessor().getId())
        .userId(review.getUser().getId())
        .username(review.getUser().getUsername())
        .rating(review.getRating())
        .comment(review.getComment())
        .createdAt(review.getCreatedAt())
        .active(review.getActive())
        .build();
  }
}
