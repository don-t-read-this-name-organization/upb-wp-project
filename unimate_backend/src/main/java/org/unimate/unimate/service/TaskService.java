package org.unimate.unimate.service;

import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.task.request.TaskRequest;
import org.unimate.unimate.api.dto.task.response.TaskResponse;
import org.unimate.unimate.api.dto.subtask.response.SubtaskResponse;
import org.unimate.unimate.domain.entities.Subtask;
import org.unimate.unimate.domain.entities.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {

  List<Task> findAll();

  Optional<Task> findById(Integer id);

  List<Task> findByUserId(Integer userId);

  Task save(Task task);

  @Transactional
  TaskResponse create(TaskRequest request);

  @Transactional
  TaskResponse update(Task task, TaskRequest request);

  void delete(Task task);

  @Transactional
  Subtask addSubtask(Integer taskId, String title);

  @Transactional
  SubtaskResponse updateSubtask(Integer subtaskId, String title, Boolean completed);

  @Transactional
  void deleteSubtask(Integer subtaskId);
}
