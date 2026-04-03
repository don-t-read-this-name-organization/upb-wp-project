package org.unimate.unimate.api.dto.schedule.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ScheduleRequest(
    @NotBlank String courseName,
    @NotBlank String dayOfWeek,
    @NotNull LocalTime startTime,
    @NotNull LocalTime endTime,
    @NotBlank String location
) {
}
