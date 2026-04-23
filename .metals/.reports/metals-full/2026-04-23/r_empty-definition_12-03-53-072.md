error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/subtask/response/SubtaskResponse.java:org/unimate/unimate/domain/entities/Subtask#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/subtask/response/SubtaskResponse.java
empty definition using pc, found symbol in pc: org/unimate/unimate/domain/entities/Subtask#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 223
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/dto/subtask/response/SubtaskResponse.java
text:
```scala
package org.unimate.unimate.api.dto.subtask.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.@@Subtask;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class SubtaskResponse {
  private Integer id;
  private Integer taskId;
  private String title;
  private Boolean completed;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static SubtaskResponse fromEntity(Subtask subtask) {
    return SubtaskResponse.builder()
        .id(subtask.getId())
        .taskId(subtask.getTask().getId())
        .title(subtask.getTitle())
        .completed(subtask.getCompleted())
        .createdAt(subtask.getCreatedAt())
        .updatedAt(subtask.getUpdatedAt())
        .build();
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/unimate/unimate/domain/entities/Subtask#