package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.schedule.request.ScheduleRequest;
import org.unimate.unimate.api.dto.schedule.response.ScheduleResponse;
import org.unimate.unimate.domain.entities.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
  ScheduleResponse create(Integer userId, ScheduleRequest request);

  List<ScheduleResponse> getUserSchedule(Integer userId);

  ScheduleResponse update(Integer scheduleId, ScheduleRequest request);

  void delete(Integer scheduleId);

  Optional<Schedule> findById(Integer scheduleId);
}
