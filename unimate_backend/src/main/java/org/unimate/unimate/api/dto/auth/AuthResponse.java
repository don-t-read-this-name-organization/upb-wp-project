package org.unimate.unimate.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.unimate.unimate.api.dto.user.response.UserResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
    private String token; // Legacy field for older frontend

    public static AuthResponse of(UserResponse user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .token(accessToken) // Legacy
                .build();
    }
}
