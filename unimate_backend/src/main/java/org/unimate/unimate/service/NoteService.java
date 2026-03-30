package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.note.NoteRequest;
import org.unimate.unimate.api.dto.note.NoteResponse;
import org.unimate.unimate.domain.entities.Note;

import java.util.List;

public interface NoteService {
    List<NoteResponse> findByUserId(Integer userId);
    List<NoteResponse> searchByUserId(Integer userId, String search);
    NoteResponse findById(Integer id);
    NoteResponse create(Integer userId, NoteRequest request);
    NoteResponse update(Integer id, NoteRequest request);
    void delete(Integer id);
}
