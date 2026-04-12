package org.unimate.unimate.service;

public interface PasswordResetService {
  String createResetToken(String email);

  void resetPassword(String rawToken, String newPassword);
}
