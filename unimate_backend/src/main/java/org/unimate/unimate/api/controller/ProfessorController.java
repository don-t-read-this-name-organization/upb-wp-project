package org.unimate.unimate.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.professor.request.ProfessorRequest;
import org.unimate.unimate.api.dto.professor.response.ProfessorResponse;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.service.ProfessorService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/professors")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProfessorController {

  ProfessorService professorService;

  @GetMapping
  @PreAuthorize("permitAll()")
  public List<ProfessorResponse> list() {
    return professorService.findAllActive().stream()
        .map(ProfessorResponse::fromEntity)
        .toList();
  }

  @GetMapping("/{id}")
  @PreAuthorize("permitAll()")
  public ProfessorResponse getById(@PathVariable Integer id) {
    return professorService.findById(id)
        .map(ProfessorResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("Professor", id));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ProfessorResponse create(@Valid @RequestBody ProfessorRequest request) {
    return ProfessorResponse.fromEntity(
        professorService.create(
            request.name().trim(),
            request.department() != null ? request.department().trim() : null,
            request.faculty() != null ? request.faculty().trim() : null
        )
    );
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ProfessorResponse update(@PathVariable Integer id, @Valid @RequestBody ProfessorRequest request) {
    return ProfessorResponse.fromEntity(
        professorService.update(
            id,
            request.name().trim(),
            request.department() != null ? request.department().trim() : null,
            request.faculty() != null ? request.faculty().trim() : null
        )
    );
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Integer id) {
    professorService.delete(id);
  }
}
