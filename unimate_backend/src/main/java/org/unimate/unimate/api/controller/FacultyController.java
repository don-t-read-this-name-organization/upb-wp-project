package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.faculty.response.FacultyResponse;
import org.unimate.unimate.domain.entities.Faculty;
import org.unimate.unimate.service.FacultyService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FacultyController {

    FacultyService facultyService;

    @GetMapping
    public List<FacultyResponse> getAll(@RequestParam(required = false, defaultValue = "en") String lang) {
        return facultyService.findWithTranslation(lang).stream()
            .map(FacultyResponse::fromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public FacultyResponse getById(@PathVariable Integer id, @RequestParam(required = false, defaultValue = "en") String lang) {
        return facultyService.findByIdWithTranslation(id, lang)
            .map(FacultyResponse::fromEntity)
            .orElse(null);
    }
}
