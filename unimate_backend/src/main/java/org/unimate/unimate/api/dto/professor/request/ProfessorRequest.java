package org.unimate.unimate.api.dto.professor.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessorRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 255) String department,
    @Size(max = 255) String faculty
) {
}
