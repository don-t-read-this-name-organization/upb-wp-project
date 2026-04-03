package org.unimate.unimate.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.schedule.request.ScheduleRequest;
import org.unimate.unimate.api.dto.schedule.response.ScheduleResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.ScheduleService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ScheduleController {

  ScheduleService scheduleService;

  @PostMapping
  @PreAuthorize("hasRole('USER')")
  public ScheduleResponse create(
      @Valid @RequestBody ScheduleRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return scheduleService.create(currentUser.getId(), request);
  }

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  public List<ScheduleResponse> list(
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return scheduleService.getUserSchedule(currentUser.getId());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsSchedule(#id)")
  public ScheduleResponse update(
      @PathVariable Integer id,
      @Valid @RequestBody ScheduleRequest request
  ) {
    return scheduleService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsSchedule(#id)")
  public void delete(@PathVariable Integer id) {
    scheduleService.delete(id);
  }
}
