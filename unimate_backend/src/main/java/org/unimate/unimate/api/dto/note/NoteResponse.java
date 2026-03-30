package org.unimate.unimate.api.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.Note;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class NoteResponse {
    Integer id;
    String title;
    String collection;
    String content;
    String description;
    LocalDateTime createdAt;

    public static NoteResponse fromEntity(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .collection(note.getCollection())
                .content(note.getContent())
                .description(note.getDescription())
                .createdAt(note.getCreatedAt())
                .build();
    }
}
