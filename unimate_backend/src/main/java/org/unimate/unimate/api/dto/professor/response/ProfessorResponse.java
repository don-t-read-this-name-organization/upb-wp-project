package org.unimate.unimate.api.dto.professor.response;

import lombok.Builder;
import org.unimate.unimate.domain.entities.Professor;

@Builder
public record ProfessorResponse(
    Integer id,
    String name,
    String department,
    String faculty,
    String phone,
    String email,
    String officeLocation,
    String officeHours,
    Boolean active
) {
  public static ProfessorResponse fromEntity(Professor professor) {
    return ProfessorResponse.builder()
        .id(professor.getId())
        .name(professor.getName())
        .department(professor.getDepartment())
        .faculty(professor.getFaculty())
        .phone(professor.getPhone())
        .email(professor.getEmail())
        .officeLocation(professor.getOfficeLocation())
        .officeHours(professor.getOfficeHours())
        .active(professor.getActive())
        .build();
  }
}
