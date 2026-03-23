package org.unimate.unimate.api.dto.subtask.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Subtask;

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

  public static SubtaskResponse fromEntity(Subtask subtask) {
    return SubtaskResponse.builder()
        .id(subtask.getId())
        .taskId(subtask.getTask().getId())
        .title(subtask.getTitle())
        .completed(subtask.getCompleted())
        .build();
  }
}
