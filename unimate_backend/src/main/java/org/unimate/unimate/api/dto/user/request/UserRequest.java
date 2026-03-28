package org.unimate.unimate.api.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.enums.RoleName;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class UserRequest {

  @Size(min = 2, max = 256)
  private String username;

  @Email
  @NotEmpty
  private String email;

  @NotEmpty(message = "Password cannot be empty")
  @Size(min = 6, max = 256)
  private String password;

  @Builder.Default
  private RoleName role = RoleName.STUDENT;

  private Integer facultyId;

  private Integer groupId;

  private String firstName;

  private String lastName;
}
