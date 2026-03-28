package org.unimate.unimate.api.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.enums.NoteContentType;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class NoteRequest {
    Integer userId;
    String title;
    String collection;
    List<NoteContentRequest> contents;

    @Data
    @AllArgsConstructor
    @Builder
    public static class NoteContentRequest {
        NoteContentType contentType;
        String content;
        String filePath;
        Integer sortOrder;
    }
}
