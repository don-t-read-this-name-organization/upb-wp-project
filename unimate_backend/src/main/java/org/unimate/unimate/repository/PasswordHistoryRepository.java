package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.PasswordHistory;

import java.util.List;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Integer> {
  List<PasswordHistory> findTop3ByUserIdOrderByChangedAtDesc(Integer userId);

  List<PasswordHistory> findByUserIdOrderByChangedAtDesc(Integer userId);
}
