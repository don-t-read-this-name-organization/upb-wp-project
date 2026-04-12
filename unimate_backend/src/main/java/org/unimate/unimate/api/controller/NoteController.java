package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.note.NoteRequest;
import org.unimate.unimate.api.dto.note.NoteResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.NoteService;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class NoteController {

    final NoteService noteService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NoteResponse>> getNotes(
            @AuthenticationPrincipal AuthenticatedUser currentUser,
            @RequestParam(required = false) String search) {
        if (currentUser == null) {
            throw new ValidationException("User is not authenticated");
        }
        Integer userId = currentUser.getId();
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(noteService.searchByUserId(userId, search));
        }
        return ResponseEntity.ok(noteService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsNote(#id)")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Integer id) {
        NoteResponse note = noteService.findById(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(note);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoteResponse> createNote(
            @RequestBody NoteRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (currentUser == null) {
            throw new ValidationException("User is not authenticated");
        }
        NoteResponse note = noteService.create(currentUser.getId(), request);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsNote(#id)")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Integer id, @RequestBody NoteRequest request) {
        NoteResponse note = noteService.update(id, request);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.ownsNote(#id)")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id) {
        noteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
