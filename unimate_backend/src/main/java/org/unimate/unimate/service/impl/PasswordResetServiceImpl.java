package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.domain.entities.PasswordHistory;
import org.unimate.unimate.domain.entities.PasswordResetToken;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.PasswordHistoryRepository;
import org.unimate.unimate.repository.PasswordResetTokenRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.PasswordPolicyService;
import org.unimate.unimate.service.PasswordResetService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PasswordResetServiceImpl implements PasswordResetService {

  private static final int TOKEN_EXPIRATION_MINUTES = 30;
  private static final int PASSWORD_HISTORY_LIMIT = 3;

  UserRepository userRepository;
  PasswordResetTokenRepository passwordResetTokenRepository;
  PasswordHistoryRepository passwordHistoryRepository;
  PasswordPolicyService passwordPolicyService;
  PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public String createResetToken(String email) {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
      return null;
    }

    List<PasswordResetToken> activeTokens = passwordResetTokenRepository.findByUserIdAndUsedFalse(user.getId());
    for (PasswordResetToken token : activeTokens) {
      token.setUsed(true);
    }
    if (!activeTokens.isEmpty()) {
      passwordResetTokenRepository.saveAll(activeTokens);
    }

    String rawToken = UUID.randomUUID().toString();
    PasswordResetToken resetToken = PasswordResetToken.builder()
        .user(user)
        .tokenHash(hash(rawToken))
        .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES))
        .used(false)
        .build();
    passwordResetTokenRepository.save(resetToken);

    return rawToken;
  }

  @Override
  @Transactional
  public void resetPassword(String rawToken, String newPassword) {
    PasswordResetToken resetToken = passwordResetTokenRepository
        .findByTokenHashAndUsedFalse(hash(rawToken))
        .orElseThrow(() -> new ValidationException("Invalid or expired reset token"));

    if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
      resetToken.setUsed(true);
      passwordResetTokenRepository.save(resetToken);
      throw new ValidationException("Invalid or expired reset token");
    }

    User user = resetToken.getUser();
    passwordPolicyService.validateNewPassword(newPassword, user.getUsername(), user.getEmail());
    List<PasswordHistory> recentHistory = passwordHistoryRepository.findTop3ByUserIdOrderByChangedAtDesc(user.getId());
    passwordPolicyService.validatePasswordReuse(newPassword, user, recentHistory);

    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPasswordHash(encodedPassword);
    user.setPasswordChangedAt(LocalDateTime.now());
    user.setPasswordExpired(false);
    user.setLockedUntil(null);
    user.setLoginAttempts(0);
    userRepository.save(user);

    resetToken.setUsed(true);
    passwordResetTokenRepository.save(resetToken);

    PasswordHistory historyEntry = PasswordHistory.builder()
        .user(user)
        .passwordHash(encodedPassword)
        .build();
    passwordHistoryRepository.save(historyEntry);
    trimPasswordHistory(user.getId());
  }

  private void trimPasswordHistory(Integer userId) {
    List<PasswordHistory> allHistory = passwordHistoryRepository.findByUserIdOrderByChangedAtDesc(userId);
    if (allHistory.size() <= PASSWORD_HISTORY_LIMIT) {
      return;
    }
    List<PasswordHistory> toDelete = allHistory.subList(PASSWORD_HISTORY_LIMIT, allHistory.size());
    passwordHistoryRepository.deleteAll(toDelete);
  }

  private String hash(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm is unavailable", e);
    }
  }
}
