package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.task.request.TaskRequest;
import org.unimate.unimate.api.dto.task.response.TaskResponse;
import org.unimate.unimate.api.dto.subtask.response.SubtaskResponse;
import org.unimate.unimate.domain.entities.Subtask;
import org.unimate.unimate.domain.entities.Task;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.repository.SubtaskRepository;
import org.unimate.unimate.repository.TaskRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskServiceImpl implements TaskService {

  TaskRepository taskRepository;
  UserRepository userRepository;
  SubtaskRepository subtaskRepository;

  @Override
  public List<Task> findAll() {
    return taskRepository.findByActiveTrueWithSubtasks();
  }

  @Override
  public Optional<Task> findById(Integer id) {
    return taskRepository.findByIdWithSubtasks(id);
  }

  @Override
  public List<Task> findByUserId(Integer userId) {
    return taskRepository.findByUserIdAndActiveTrueWithSubtasks(userId);
  }

  @Override
  public Task save(Task task) {
    return taskRepository.save(task);
  }

  @Transactional
  @Override
  public TaskResponse create(TaskRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Task task = Task.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .status(request.getStatus() != null ? request.getStatus().name() : null)
        .priority(request.getPriority() != null ? request.getPriority().getValue() : null)
        .kanbanColumn(request.getKanbanColumn())
        .deadline(request.getDeadline())
        .user(user)
        .subtasks(new ArrayList<>())
        .build();

    if (request.getSubtasks() != null && !request.getSubtasks().isEmpty()) {
      for (var subtaskReq : request.getSubtasks()) {
        Subtask subtask = Subtask.builder()
            .task(task)
            .title(subtaskReq.getTitle())
            .completed(subtaskReq.getCompleted() != null ? subtaskReq.getCompleted() : false)
            .build();
        task.getSubtasks().add(subtask);
      }
    }

    Task savedTask = save(task);
    return TaskResponse.fromEntity(savedTask);
  }

  @Transactional
  @Override
  public TaskResponse update(Task task, TaskRequest request) {
    if (request.getTitle() != null) {
      task.setTitle(request.getTitle());
    }
    if (request.getDescription() != null) {
      task.setDescription(request.getDescription());
    }
    if (request.getStatus() != null) {
      task.setStatus(request.getStatus().name());
    }
    if (request.getPriority() != null) {
      task.setPriority(request.getPriority().getValue());
    }
    if (request.getKanbanColumn() != null) {
      task.setKanbanColumn(request.getKanbanColumn());
    }
    if (request.getDeadline() != null) {
      task.setDeadline(request.getDeadline());
    }
    if (request.getUserId() != null) {
      User user = userRepository.findById(request.getUserId())
          .orElseThrow(() -> new IllegalArgumentException("User not found"));
      task.setUser(user);
    }

    if (request.getSubtasks() != null) {
      List<Subtask> existingSubtasks = new ArrayList<>(task.getSubtasks());
      List<Integer> requestSubtaskIds = new ArrayList<>();

      for (var subtaskReq : request.getSubtasks()) {
        if (subtaskReq.getId() != null) {
          // Find in already-loaded subtasks to avoid duplicate managed entities
          Subtask existingSubtask = task.getSubtasks().stream()
              .filter(s -> s.getId().equals(subtaskReq.getId()))
              .findFirst()
              .orElse(null);
          
          if (existingSubtask != null) {
            existingSubtask.setTitle(subtaskReq.getTitle());
            existingSubtask.setCompleted(subtaskReq.getCompleted() != null ? subtaskReq.getCompleted() : false);
            requestSubtaskIds.add(subtaskReq.getId());
          } else {
            // Subtask doesn't belong to this task - create new one
            Subtask subtask = Subtask.builder()
                .task(task)
                .title(subtaskReq.getTitle())
                .completed(subtaskReq.getCompleted() != null ? subtaskReq.getCompleted() : false)
                .build();
            task.getSubtasks().add(subtask);
          }
        } else {
          // New subtask
          Subtask subtask = Subtask.builder()
              .task(task)
              .title(subtaskReq.getTitle())
              .completed(subtaskReq.getCompleted() != null ? subtaskReq.getCompleted() : false)
              .build();
          task.getSubtasks().add(subtask);
        }
      }

      for (Subtask existing : existingSubtasks) {
        if (!requestSubtaskIds.contains(existing.getId())) {
          task.getSubtasks().remove(existing);
          subtaskRepository.delete(existing);
        }
      }
    }
    Task updatedTask = save(task);
    return TaskResponse.fromEntity(updatedTask);
  }

  @Override
  public void delete(Task task) {
    task.setActive(false);
    save(task);
  }

  @Transactional
  @Override
  public Subtask addSubtask(Integer taskId, String title) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new IllegalArgumentException("Task not found"));

    Subtask subtask = Subtask.builder()
        .task(task)
        .title(title)
        .completed(false)
        .build();

    task.getSubtasks().add(subtask);
    taskRepository.save(task);

    return subtask;
  }

  @Transactional
  @Override
  public SubtaskResponse updateSubtask(Integer subtaskId, String title, Boolean completed) {
    Subtask subtask = subtaskRepository.findById(subtaskId)
        .orElseThrow(() -> new IllegalArgumentException("Subtask not found"));

    if (title != null) {
      subtask.setTitle(title);
    }
    if (completed != null) {
      subtask.setCompleted(completed);
    }

    subtaskRepository.save(subtask);
    return SubtaskResponse.fromEntity(subtask);
  }

  @Transactional
  @Override
  public void deleteSubtask(Integer subtaskId) {
    Subtask subtask = subtaskRepository.findById(subtaskId)
        .orElseThrow(() -> new IllegalArgumentException("Subtask not found"));
    subtaskRepository.delete(subtask);
  }
}
