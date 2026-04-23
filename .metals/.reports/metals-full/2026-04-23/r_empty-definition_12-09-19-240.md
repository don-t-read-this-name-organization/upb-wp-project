error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/user/request/UserRequest.java:jakarta/validation/constraints/NotEmpty#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/user/request/UserRequest.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/NotEmpty#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 137
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/user/request/UserRequest.java
text:
```scala
package org.unimate.unimate.api.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.@@NotEmpty;
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

  @Size(min = 6, max = 256)
  private String password;

  @Builder.Default
  private RoleName role = RoleName.STUDENT;

  private Integer facultyId;

  private Integer groupId;

  private String firstName;

  private String lastName;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/NotEmpty#