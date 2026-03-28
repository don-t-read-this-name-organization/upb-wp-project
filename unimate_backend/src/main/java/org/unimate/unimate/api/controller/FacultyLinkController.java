package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.faculty.FacultyLinkWithTranslation;
import org.unimate.unimate.service.FacultyLinkService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/faculty-links")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class FacultyLinkController {
    final FacultyLinkService facultyLinkService;

    @GetMapping("/{facultyId}")
    public ResponseEntity<List<FacultyLinkWithTranslation>> getFacultyLinks(
            @PathVariable Integer facultyId,
            @RequestParam(defaultValue = "en") String lang) {
        
        List<FacultyLinkWithTranslation> links = facultyLinkService.findByFacultyIdWithTranslation(facultyId, lang);
        return ResponseEntity.ok(links);
    }
}
