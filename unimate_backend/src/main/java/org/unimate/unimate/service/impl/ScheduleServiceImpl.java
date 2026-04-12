package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.schedule.request.ScheduleRequest;
import org.unimate.unimate.api.dto.schedule.response.ScheduleResponse;
import org.unimate.unimate.domain.entities.Schedule;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.ScheduleRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.ScheduleService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ScheduleServiceImpl implements ScheduleService {

  ScheduleRepository scheduleRepository;
  UserRepository userRepository;

  @Override
  @Transactional
  public ScheduleResponse create(Integer userId, ScheduleRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User", userId));

    String normalizedDayOfWeek = normalizeDayOfWeek(request.dayOfWeek());
    validateTimeRange(request.startTime(), request.endTime());
    ensureNoConflict(userId, normalizedDayOfWeek, request.startTime(), request.endTime(), null);

    Schedule schedule = Schedule.builder()
        .user(user)
        .courseName(request.courseName().trim())
        .dayOfWeek(normalizedDayOfWeek)
        .startTime(request.startTime())
        .endTime(request.endTime())
        .location(request.location().trim())
        .active(true)
        .build();

    return ScheduleResponse.fromEntity(scheduleRepository.save(schedule));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ScheduleResponse> getUserSchedule(Integer userId) {
    return scheduleRepository.findByUserIdAndActiveTrueOrderByDayOfWeekAscStartTimeAsc(userId)
        .stream()
        .map(ScheduleResponse::fromEntity)
        .toList();
  }

  @Override
  @Transactional
  public ScheduleResponse update(Integer scheduleId, ScheduleRequest request) {
    Schedule schedule = scheduleRepository.findByIdAndActiveTrue(scheduleId)
        .orElseThrow(() -> new NotFoundException("Schedule", scheduleId));

    String normalizedDayOfWeek = normalizeDayOfWeek(request.dayOfWeek());
    validateTimeRange(request.startTime(), request.endTime());
    ensureNoConflict(
        schedule.getUser().getId(),
        normalizedDayOfWeek,
        request.startTime(),
        request.endTime(),
        scheduleId
    );

    schedule.setCourseName(request.courseName().trim());
    schedule.setDayOfWeek(normalizedDayOfWeek);
    schedule.setStartTime(request.startTime());
    schedule.setEndTime(request.endTime());
    schedule.setLocation(request.location().trim());

    return ScheduleResponse.fromEntity(scheduleRepository.save(schedule));
  }

  @Override
  @Transactional
  public void delete(Integer scheduleId) {
    Schedule schedule = scheduleRepository.findByIdAndActiveTrue(scheduleId)
        .orElseThrow(() -> new NotFoundException("Schedule", scheduleId));

    schedule.setActive(false);
    scheduleRepository.save(schedule);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Schedule> findById(Integer scheduleId) {
    return scheduleRepository.findById(scheduleId);
  }

  private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
    if (!startTime.isBefore(endTime)) {
      throw new ValidationException("startTime must be before endTime");
    }
  }

  private String normalizeDayOfWeek(String dayOfWeek) {
    String normalized = dayOfWeek.trim().toUpperCase(Locale.ROOT);
    try {
      DayOfWeek.valueOf(normalized);
      return normalized;
    } catch (IllegalArgumentException ex) {
      throw new ValidationException("dayOfWeek must be one of MONDAY..SUNDAY");
    }
  }

  private void ensureNoConflict(
      Integer userId,
      String dayOfWeek,
      LocalTime startTime,
      LocalTime endTime,
      Integer ignoreScheduleId
  ) {
    List<Schedule> overlaps = scheduleRepository
        .findByUserIdAndDayOfWeekAndActiveTrueAndStartTimeLessThanAndEndTimeGreaterThan(
            userId,
            dayOfWeek,
            endTime,
            startTime
        );

    boolean hasConflict = overlaps.stream()
        .anyMatch(existing -> ignoreScheduleId == null || !existing.getId().equals(ignoreScheduleId));

    if (hasConflict) {
      throw new ValidationException("Schedule overlaps with another course in the same day");
    }
  }
}
