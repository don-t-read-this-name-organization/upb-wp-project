package org.unimate.unimate.api.dto.auth.response;

import org.unimate.unimate.api.dto.user.response.UserResponse;

public record AuthTokenResponse(
    UserResponse user,
    String accessToken,
    long accessTokenExpiresIn,
    String refreshToken,
    long refreshTokenExpiresIn,
    String tokenType
) {
  public static AuthTokenResponse of(
      UserResponse user,
      String accessToken,
      long accessTokenExpiresIn,
      String refreshToken,
      long refreshTokenExpiresIn
  ) {
    return new AuthTokenResponse(
        user,
        accessToken,
        accessTokenExpiresIn,
        refreshToken,
        refreshTokenExpiresIn,
        "Bearer"
    );
  }
}
