package org.unimate.unimate.api.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Faculty;
import org.unimate.unimate.domain.entities.Group;
import org.unimate.unimate.domain.entities.User;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class UserResponse {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role; // STUDENT, CHIEF, ADMIN, VISITOR
    private Boolean active;
    private FacultyInfo faculty;
    private GroupInfo group;

    @Data
    @AllArgsConstructor
    @Builder
    public static class FacultyInfo {
        private Integer id;
        private String name;
        private String shortName;
        private String website;

        public static FacultyInfo fromEntity(Faculty faculty) {
            if (faculty == null) return null;
            return FacultyInfo.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .shortName(faculty.getShortName())
                .website(faculty.getWebsite())
                .build();
        }
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class GroupInfo {
        private Integer id;
        private String name;
        private Integer year;

        public static GroupInfo fromEntity(Group group) {
            if (group == null) return null;
            return GroupInfo.builder()
                .id(group.getId())
                .name(group.getName())
                .year(group.getYear())
                .build();
        }
    }

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole().name())
            .active(user.getActive())
            .faculty(FacultyInfo.fromEntity(user.getFaculty()))
            .group(GroupInfo.fromEntity(user.getStudyGroup()))
            .build();
    }
}
