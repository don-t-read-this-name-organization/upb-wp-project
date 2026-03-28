package org.unimate.unimate.api.dto.group.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Group;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class GroupResponse {
    private Integer id;
    private String name;
    private Integer year;
    private Integer facultyId;

    public static GroupResponse fromEntity(Group group) {
        return GroupResponse.builder()
            .id(group.getId())
            .name(group.getName())
            .year(group.getYear())
            .facultyId(group.getFaculty().getId())
            .build();
    }
}
