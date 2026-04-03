package org.unimate.unimate.api.dto.timetable.response;

import lombok.Builder;
import org.unimate.unimate.domain.entities.Timetable;

import java.time.LocalDateTime;

@Builder
public record TimetableResponse(
    Integer id,
    Integer userId,
    String filename,
    LocalDateTime uploadedAt,
    Boolean active
) {
  public static TimetableResponse fromEntity(Timetable timetable) {
    return TimetableResponse.builder()
        .id(timetable.getId())
        .userId(timetable.getUser().getId())
        .filename(timetable.getFilename())
        .uploadedAt(timetable.getUploadedAt())
        .active(timetable.getActive())
        .build();
  }
}
