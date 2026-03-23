package org.unimate.unimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Task;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {
  List<Task> findByUserIdAndActiveTrue(Integer userId);
  List<Task> findByActiveTrue();
}
