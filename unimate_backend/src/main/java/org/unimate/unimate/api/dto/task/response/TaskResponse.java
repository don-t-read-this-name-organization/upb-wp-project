package org.unimate.unimate.api.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.api.dto.subtask.response.SubtaskResponse;
import org.unimate.unimate.domain.entities.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private Integer priority;
    private String kanbanColumn;
    private LocalDate deadline;
    private Integer userId;
    private LocalDateTime createdAt;
    private Boolean active;
    private List<SubtaskResponse> subtasks;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus())
            .priority(task.getPriority())
            .kanbanColumn(task.getKanbanColumn())
            .deadline(task.getDeadline())
            .userId(task.getUser() != null ? task.getUser().getId() : null)
            .createdAt(task.getCreatedAt())
            .active(task.getActive())
            .subtasks(task.getSubtasks() != null 
                ? task.getSubtasks().stream().map(SubtaskResponse::fromEntity).toList()
                : null)
            .build();
    }
}
