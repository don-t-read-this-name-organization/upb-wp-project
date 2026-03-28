package org.unimate.unimate.api.dto.faculty.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Faculty;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class FacultyResponse {
    private Integer id;
    private String name;
    private String shortName;
    private String website;

    public static FacultyResponse fromEntity(Faculty faculty) {
        return FacultyResponse.builder()
            .id(faculty.getId())
            .name(faculty.getName())
            .shortName(faculty.getShortName())
            .website(faculty.getWebsite())
            .build();
    }
}
