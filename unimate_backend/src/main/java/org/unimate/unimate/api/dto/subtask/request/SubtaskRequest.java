package org.unimate.unimate.api.dto.subtask.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class SubtaskRequest {
  private Integer id;
  private String title;
  private Boolean completed;
}
