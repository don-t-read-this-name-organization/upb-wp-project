package org.unimate.unimate.api.dto.schedule.response;

import lombok.Builder;
import org.unimate.unimate.domain.entities.Schedule;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record ScheduleResponse(
    Integer id,
    Integer userId,
    String courseName,
    String dayOfWeek,
    LocalTime startTime,
    LocalTime endTime,
    String location,
    LocalDateTime createdAt,
    Boolean active
) {
  public static ScheduleResponse fromEntity(Schedule schedule) {
    return ScheduleResponse.builder()
        .id(schedule.getId())
        .userId(schedule.getUser().getId())
        .courseName(schedule.getCourseName())
        .dayOfWeek(schedule.getDayOfWeek())
        .startTime(schedule.getStartTime())
        .endTime(schedule.getEndTime())
        .location(schedule.getLocation())
        .createdAt(schedule.getCreatedAt())
        .active(schedule.getActive())
        .build();
  }
}
