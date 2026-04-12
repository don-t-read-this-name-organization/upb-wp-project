package org.unimate.unimate.service;

import org.unimate.unimate.domain.entities.PasswordHistory;
import org.unimate.unimate.domain.entities.User;

import java.util.List;

public interface PasswordPolicyService {
  void validateNewPassword(String rawPassword, String username, String email);

  void validatePasswordReuse(String rawPassword, User user, List<PasswordHistory> recentHistory);

  boolean isPasswordExpired(User user);
}
