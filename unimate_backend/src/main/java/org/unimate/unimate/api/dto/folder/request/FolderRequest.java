package org.unimate.unimate.api.dto.folder.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderRequest {
    private String name;
    private Integer parentId;
}
