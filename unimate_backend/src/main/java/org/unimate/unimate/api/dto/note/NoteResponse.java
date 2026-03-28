package org.unimate.unimate.api.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Note;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class NoteResponse {
    Integer id;
    String title;
    String collection;
    List<NoteContentResponse> contents;
    LocalDateTime createdAt;

    public static NoteResponse fromEntity(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .collection(note.getCollection())
                .contents(note.getContents().stream()
                        .map(NoteContentResponse::fromEntity)
                        .toList())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
