package org.unimate.unimate.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.timetable.request.TimetableRequest;
import org.unimate.unimate.api.dto.timetable.response.TimetableResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.TimetableService;

import java.io.IOException;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TimetableController {

  TimetableService timetableService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasRole('USER')")
  public TimetableResponse uploadTimetable(
      @Valid @ModelAttribute TimetableRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return timetableService.uploadTimetable(currentUser.getId(), request.file());
  }

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Resource> getTimetable(
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) throws IOException {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }

    TimetableResponse metadata = timetableService.getTimetableMetadata(currentUser.getId());
    Resource resource = timetableService.getTimetable(currentUser.getId());

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.filename() + "\"")
        .body(resource);
  }

  @DeleteMapping
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Void> deleteTimetable(
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    timetableService.deleteTimetable(currentUser.getId());
    return ResponseEntity.noContent().build();
  }
}
