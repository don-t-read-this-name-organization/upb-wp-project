package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.PasswordResetToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
  Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

  List<PasswordResetToken> findByUserIdAndUsedFalse(Integer userId);
}
