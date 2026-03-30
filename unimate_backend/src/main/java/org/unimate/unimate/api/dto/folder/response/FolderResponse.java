package org.unimate.unimate.api.dto.folder.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unimate.unimate.domain.entities.Folder;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponse {
    private Integer id;
    private String name;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FolderResponse fromEntity(Folder folder) {
        return FolderResponse.builder()
                .id(folder.getId())
                .name(folder.getName())
                .parentId(folder.getParent() != null ? folder.getParent().getId() : null)
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }
}
