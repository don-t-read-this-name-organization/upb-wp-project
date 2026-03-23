package org.unimate.unimate.api.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.api.dto.subtask.request.SubtaskRequest;
import org.unimate.unimate.domain.enums.TaskPriority;
import org.unimate.unimate.domain.enums.TaskStatus;

import java.time.LocalDate;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class TaskRequest {

  private String title;

  private String description;

  private TaskStatus status;

  private TaskPriority priority;

  private String kanbanColumn;

  private LocalDate deadline;

  private Integer userId;

  private List<SubtaskRequest> subtasks;
}
