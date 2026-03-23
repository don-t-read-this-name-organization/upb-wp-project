package org.unimate.unimate.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Subtask;

import java.util.List;

@Repository
public interface SubtaskRepository extends CrudRepository<Subtask, Integer> {
  List<Subtask> findByTaskIdAndTask_ActiveTrue(Integer taskId);
  List<Subtask> findByTaskId(Integer taskId);
}
