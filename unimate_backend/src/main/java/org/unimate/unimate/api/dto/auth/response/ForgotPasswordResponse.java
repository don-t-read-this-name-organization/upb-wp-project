package org.unimate.unimate.api.dto.auth.response;

public record ForgotPasswordResponse(
    String message,
    String resetToken
) {
}
