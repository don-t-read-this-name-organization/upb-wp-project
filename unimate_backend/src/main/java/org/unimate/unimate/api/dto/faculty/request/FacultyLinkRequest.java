package org.unimate.unimate.api.dto.faculty.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FacultyLinkRequest(
    @NotNull Integer facultyId,
    @NotBlank @Size(max = 2000) String url,
    @Size(max = 100) String icon,
    @Size(max = 50) String colorClass
) {
}
