package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.note.NoteRequest;
import org.unimate.unimate.api.dto.note.NoteResponse;
import org.unimate.unimate.domain.entities.Note;
import org.unimate.unimate.domain.entities.NoteContent;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.repository.NoteContentRepository;
import org.unimate.unimate.repository.NoteRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.NoteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class NoteServiceImpl implements NoteService {

    NoteRepository noteRepository;
    NoteContentRepository noteContentRepository;
    UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NoteResponse> findByUserId(Integer userId) {
        return noteRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NoteResponse findById(Integer id) {
        return noteRepository.findByIdWithContents(id)
                .map(NoteResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional
    public NoteResponse create(Integer userId, NoteRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        
        Note note = Note.builder()
                .user(user)
                .title(request.getTitle())
                .collection(request.getCollection())
                .contents(new ArrayList<>())
                .active(true)
                .build();

        if (request.getContents() != null) {
            for (NoteRequest.NoteContentRequest contentReq : request.getContents()) {
                NoteContent content = NoteContent.builder()
                        .note(note)
                        .contentType(contentReq.getContentType())
                        .content(contentReq.getContent())
                        .filePath(contentReq.getFilePath())
                        .sortOrder(contentReq.getSortOrder())
                        .build();
                note.getContents().add(content);
            }
        }

        note = noteRepository.save(note);
        return NoteResponse.fromEntity(note);
    }

    @Override
    @Transactional
    public NoteResponse update(Integer id, NoteRequest request) {
        Note note = noteRepository.findById(id).orElseThrow();
        
        note.setTitle(request.getTitle());
        note.setCollection(request.getCollection());

        note.getContents().clear();
        if (request.getContents() != null) {
            for (NoteRequest.NoteContentRequest contentReq : request.getContents()) {
                NoteContent content = NoteContent.builder()
                        .note(note)
                        .contentType(contentReq.getContentType())
                        .content(contentReq.getContent())
                        .filePath(contentReq.getFilePath())
                        .sortOrder(contentReq.getSortOrder())
                        .build();
                note.getContents().add(content);
            }
        }

        note = noteRepository.save(note);
        return NoteResponse.fromEntity(note);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        noteRepository.findById(id).ifPresent(note -> {
            note.setActive(false);
            noteRepository.save(note);
        });
    }
}
