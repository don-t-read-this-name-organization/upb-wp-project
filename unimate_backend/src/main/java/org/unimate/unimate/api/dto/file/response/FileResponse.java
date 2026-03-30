package org.unimate.unimate.api.dto.file.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.File;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class FileResponse {
    Integer id;
    String filename;
    String displayName;
    String filePath;
    String fileType;
    Long fileSize;
    Integer folderId;
    LocalDateTime createdAt;

    public static FileResponse fromEntity(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .filename(file.getFilename())
                .displayName(file.getDisplayName())
                .filePath(file.getFilePath())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .folderId(file.getFolder() != null ? file.getFolder().getId() : null)
                .createdAt(file.getCreatedAt())
                .build();
    }
}
