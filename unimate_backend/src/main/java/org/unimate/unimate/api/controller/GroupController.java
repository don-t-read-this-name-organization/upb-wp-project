package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.group.response.GroupResponse;
import org.unimate.unimate.service.GroupService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class GroupController {

    GroupService groupService;

    @GetMapping
    public List<GroupResponse> getAll(@RequestParam(required = false, defaultValue = "en") String lang) {
        return groupService.findWithTranslation(lang).stream()
            .map(GroupResponse::fromEntity)
            .toList();
    }

    @GetMapping("/faculty/{facultyId}")
    public List<GroupResponse> getByFaculty(@PathVariable Integer facultyId, @RequestParam(required = false, defaultValue = "en") String lang) {
        return groupService.findByFacultyIdWithTranslation(facultyId, lang).stream()
            .map(GroupResponse::fromEntity)
            .toList();
    }

    @GetMapping("/{id}")
    public GroupResponse getById(@PathVariable Integer id) {
        return groupService.findById(id)
            .map(GroupResponse::fromEntity)
            .orElse(null);
    }
}
