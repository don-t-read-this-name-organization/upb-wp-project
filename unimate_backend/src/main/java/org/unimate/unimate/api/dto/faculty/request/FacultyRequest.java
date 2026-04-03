package org.unimate.unimate.api.dto.faculty.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FacultyRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 50) String shortName,
    @NotBlank @Size(max = 2000) String website
) {
}
