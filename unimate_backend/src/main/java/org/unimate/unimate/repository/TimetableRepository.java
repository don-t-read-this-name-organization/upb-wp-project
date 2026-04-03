package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Timetable;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Integer> {
  Optional<Timetable> findByUserIdAndActive(Integer userId, boolean active);

  List<Timetable> findByUserId(Integer userId);
}
