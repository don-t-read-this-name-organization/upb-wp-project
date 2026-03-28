package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Task;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {
  List<Task> findByUserIdAndActiveTrue(Integer userId);
  List<Task> findByActiveTrue();

  @Query("SELECT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.user.id = :userId AND t.active = true")
  List<Task> findByUserIdAndActiveTrueWithSubtasks(Integer userId);

  @Query("SELECT t FROM Task t LEFT JOIN FETCH t.subtasks WHERE t.active = true")
  List<Task> findByActiveTrueWithSubtasks();
}
