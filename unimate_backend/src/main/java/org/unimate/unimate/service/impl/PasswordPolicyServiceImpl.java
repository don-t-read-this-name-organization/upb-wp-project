package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.unimate.unimate.domain.entities.PasswordHistory;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.PasswordPolicyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

  private static final Pattern HAS_UPPER = Pattern.compile(".*[A-Z].*");
  private static final Pattern HAS_LOWER = Pattern.compile(".*[a-z].*");
  private static final Pattern HAS_DIGIT = Pattern.compile(".*\\d.*");
  private static final Pattern HAS_SPECIAL = Pattern.compile(".*[^A-Za-z0-9].*");
  private static final int MIN_LENGTH = 8;

  private final PasswordEncoder passwordEncoder;

  @Value("${password.expiration-days:90}")
  private int passwordExpirationDays;

  @Override
  public void validateNewPassword(String rawPassword, String username, String email) {
    if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
      throw new ValidationException("Password must be at least 8 characters long");
    }
    if (!HAS_UPPER.matcher(rawPassword).matches()) {
      throw new ValidationException("Password must contain at least one uppercase character");
    }
    if (!HAS_LOWER.matcher(rawPassword).matches()) {
      throw new ValidationException("Password must contain at least one lowercase character");
    }
    if (!HAS_DIGIT.matcher(rawPassword).matches()) {
      throw new ValidationException("Password must contain at least one number");
    }
    if (!HAS_SPECIAL.matcher(rawPassword).matches()) {
      throw new ValidationException("Password must contain at least one special character");
    }

    String lowerPassword = rawPassword.toLowerCase(Locale.ROOT);
    if (username != null && !username.isBlank() && lowerPassword.contains(username.toLowerCase(Locale.ROOT))) {
      throw new ValidationException("Password must not contain the username");
    }
    if (email != null && !email.isBlank()) {
      String localPart = email.split("@")[0];
      if (!localPart.isBlank() && lowerPassword.contains(localPart.toLowerCase(Locale.ROOT))) {
        throw new ValidationException("Password must not contain parts of the email");
      }
    }
  }

  @Override
  public void validatePasswordReuse(String rawPassword, User user, List<PasswordHistory> recentHistory) {
    if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw new ValidationException("New password must be different from current password");
    }
    boolean reused = recentHistory.stream()
        .anyMatch(entry -> passwordEncoder.matches(rawPassword, entry.getPasswordHash()));
    if (reused) {
      throw new ValidationException("You cannot reuse one of your last 3 passwords");
    }
  }

  @Override
  public boolean isPasswordExpired(User user) {
    LocalDateTime changedAt = user.getPasswordChangedAt() != null
        ? user.getPasswordChangedAt()
        : user.getCreatedAt();
    if (changedAt == null) {
      return false;
    }
    return changedAt.plusDays(passwordExpirationDays).isBefore(LocalDateTime.now());
  }
}
