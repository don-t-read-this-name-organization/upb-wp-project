package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
  List<Review> findByProfessorIdAndActiveOrderByCreatedAtDesc(Integer professorId, boolean active);

  Optional<Review> findByUserIdAndProfessorId(Integer userId, Integer professorId);

  List<Review> findByProfessorId(Integer professorId);
}
