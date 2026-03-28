package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.note.NoteRequest;
import org.unimate.unimate.api.dto.note.NoteResponse;
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
    public ResponseEntity<List<NoteResponse>> getNotes(@RequestParam Integer userId) {
        return ResponseEntity.ok(noteService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNote(@PathVariable Integer id) {
        NoteResponse note = noteService.findById(id);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(note);
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteRequest request) {
        NoteResponse note = noteService.create(request.getUserId(), request);
        return ResponseEntity.ok(note);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Integer id, @RequestBody NoteRequest request) {
        NoteResponse note = noteService.update(id, request);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id) {
        noteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
