package org.unimate.unimate.api.dto.group.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GroupRequest(
    @NotBlank @Size(max = 50) String name,
    @NotNull @Min(1) Integer year,
    @NotNull Integer facultyId
) {
}
