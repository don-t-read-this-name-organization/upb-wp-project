package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Schedule;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
  List<Schedule> findByUserIdAndActiveTrue(Integer userId);

  List<Schedule> findByUserIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(Integer userId);

  Optional<Schedule> findByIdAndActiveTrue(Integer id);

  List<Schedule> findByUserIdAndDayOfWeekAndActiveTrueAndStartTimeLessThanAndEndTimeGreaterThan(
      Integer userId,
      String dayOfWeek,
      LocalTime endTime,
      LocalTime startTime
  );
}
