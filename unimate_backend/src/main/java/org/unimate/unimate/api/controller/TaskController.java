package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.task.request.TaskRequest;
import org.unimate.unimate.api.dto.task.response.TaskResponse;
import org.unimate.unimate.domain.entities.Subtask;
import org.unimate.unimate.domain.entities.Task;
import org.unimate.unimate.exception.NotFoundException;
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
  public TaskResponse create(@RequestBody TaskRequest request) {
    return taskService.create(request);
  }

  @GetMapping("/{id}")
  public TaskResponse getById(@PathVariable Integer id) {
    return taskService.findById(id).map(TaskResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("Task", id));
  }

  @GetMapping
  public List<TaskResponse> list() {
    return taskService.findAll().stream().map(TaskResponse::fromEntity).toList();
  }

  @GetMapping("/user/{userId}")
  public List<TaskResponse> getByUserId(@PathVariable Integer userId) {
    return taskService.findByUserId(userId).stream().map(TaskResponse::fromEntity).toList();
  }

  @PutMapping("/{id}")
  public TaskResponse update(@PathVariable Integer id, @RequestBody TaskRequest request) {
    Task task = taskService.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    return taskService.update(task, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    Task task = taskService.findById(id).orElseThrow(() -> new NotFoundException("Task", id));
    taskService.delete(task);
  }

  @PostMapping("/{id}/subtasks")
  public Subtask addSubtask(@PathVariable Integer id, @RequestBody Map<String, String> request) {
    String title = request.get("title");
    return taskService.addSubtask(id, title);
  }

  @PutMapping("/subtasks/{subtaskId}")
  public Subtask updateSubtask(@PathVariable Integer subtaskId, @RequestBody Map<String, Object> request) {
    String title = (String) request.get("title");
    Boolean completed = request.get("completed") != null ? (Boolean) request.get("completed") : null;
    return taskService.updateSubtask(subtaskId, title, completed);
  }

  @DeleteMapping("/subtasks/{subtaskId}")
  public void deleteSubtask(@PathVariable Integer subtaskId) {
    taskService.deleteSubtask(subtaskId);
  }
}
