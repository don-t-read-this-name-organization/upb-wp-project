package org.unimate.unimate.api.dto.faculty.response;

import lombok.Builder;
import org.unimate.unimate.domain.entities.FacultyLink;

import java.time.LocalDateTime;

@Builder
public record FacultyLinkResponse(
    Integer id,
    Integer facultyId,
    String url,
    String icon,
    String colorClass,
    LocalDateTime createdAt,
    Boolean active
) {
  public static FacultyLinkResponse fromEntity(FacultyLink link) {
    return FacultyLinkResponse.builder()
        .id(link.getId())
        .facultyId(link.getFaculty() != null ? link.getFaculty().getId() : null)
        .url(link.getUrl())
        .icon(link.getIcon())
        .colorClass(link.getColorClass())
        .createdAt(link.getCreatedAt())
        .active(link.getActive())
        .build();
  }
}
