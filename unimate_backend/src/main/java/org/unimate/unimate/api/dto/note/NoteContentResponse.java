package org.unimate.unimate.api.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.NoteContent;
import org.unimate.unimate.domain.enums.NoteContentType;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class NoteContentResponse {
    Integer id;
    NoteContentType contentType;
    String content;
    String filePath;
    Integer sortOrder;

    public static NoteContentResponse fromEntity(NoteContent noteContent) {
        return NoteContentResponse.builder()
                .id(noteContent.getId())
                .contentType(noteContent.getContentType())
                .content(noteContent.getContent())
                .filePath(noteContent.getFilePath())
                .sortOrder(noteContent.getSortOrder())
                .build();
    }
}
