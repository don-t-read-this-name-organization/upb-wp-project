package org.unimate.unimate.api.dto.timetable.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record TimetableRequest(
    @NotNull MultipartFile file
) {
}
