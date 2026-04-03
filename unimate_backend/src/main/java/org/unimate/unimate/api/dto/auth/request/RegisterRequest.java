package org.unimate.unimate.api.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 2, max = 256) String username,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 256) String password,
    String firstName,
    String lastName,
    Integer facultyId,
    Integer groupId
) {
}
