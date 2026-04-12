package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.api.dto.task.request.TaskRequest;
import org.unimate.unimate.api.dto.task.response.TaskResponse;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.domain.entities.Subtask;
import org.unimate.unimate.domain.entities.Task;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.TaskService;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TaskController {

  TaskService taskService;

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public TaskResponse create(
      @RequestBody TaskRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    if (currentUser.getRole() != RoleName.ADMIN || request.getUserId() == null) {
      request.setUserId(currentUser.getId());
    }
    return taskService.create(request);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsTask(#id)")
  public TaskResponse getById(@PathVariable Integer id) {
    return taskService.findById(id).map(TaskResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("Task", id));
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<TaskResponse> list(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    if (currentUser.getRole() == RoleName.ADMIN) {
      return taskService.findAll().stream().map(TaskResponse::fromEntity).toList();
    }
    return taskService.findByUserId(currentUser.getId()).stream().map(TaskResponse::fromEntity).toList();
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#userId)")
  public List<TaskResponse> getByUserId(@PathVariable Integer userId) {
    return taskService.findByUserId(userId).stream().map(TaskResponse::fromEntity).toList();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsTask(#id)")
  public TaskResponse update(
      @PathVariable Integer id,
      @RequestBody TaskRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    if (currentUser.getRole() != RoleName.ADMIN) {
      request.setUserId(null);
    }
    Task task = taskService.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    return taskService.update(task, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsTask(#id)")
  public void delete(@PathVariable Integer id) {
    Task task = taskService.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    taskService.delete(task);
  }

  @PostMapping("/{id}/subtasks")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsTask(#id)")
  public Subtask addSubtask(@PathVariable Integer id, @RequestBody Map<String, String> request) {
    String title = request.get("title");
    return taskService.addSubtask(id, title);
  }

  @PutMapping("/subtasks/{subtaskId}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsSubtask(#subtaskId)")
  public Subtask updateSubtask(@PathVariable Integer subtaskId, @RequestBody Map<String, Object> request) {
    String title = (String) request.get("title");
    Boolean completed = request.get("completed") != null ? (Boolean) request.get("completed") : null;
    return taskService.updateSubtask(subtaskId, title, completed);
  }

  @DeleteMapping("/subtasks/{subtaskId}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsSubtask(#subtaskId)")
  public void deleteSubtask(@PathVariable Integer subtaskId) {
    taskService.deleteSubtask(subtaskId);
  }
}
