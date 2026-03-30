package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.file.FileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    List<FileResponse> findByUserId(Integer userId);
    List<FileResponse> findByUserIdAndFolderId(Integer userId, Integer folderId);
    List<FileResponse> findByUserIdAndFileType(Integer userId, String fileType);
    FileResponse findById(Integer id);
    FileResponse findByUserIdAndFilename(Integer userId, String filename);
    FileResponse findByUserIdAndFilePath(Integer userId, String filename);
    FileResponse upload(Integer userId, MultipartFile file, Integer folderId);
    FileResponse updateDisplayName(Integer id, String displayName);
    FileResponse moveToFolder(Integer id, Integer folderId);
    Resource downloadFile(Integer id) throws IOException;
    void delete(Integer id);
}
