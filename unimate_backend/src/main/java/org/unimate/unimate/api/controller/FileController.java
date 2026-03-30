package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.unimate.unimate.api.dto.file.FileRequest;
import org.unimate.unimate.api.dto.file.FileResponse;
import org.unimate.unimate.service.FileService;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class FileController {

    final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileResponse>> getFiles(
            @RequestParam Integer userId,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) Integer folderId) {
        List<FileResponse> files;
        if (fileType != null && !fileType.isEmpty()) {
            files = fileService.findByUserIdAndFileType(userId, fileType);
        } else {
            files = fileService.findByUserIdAndFolderId(userId, folderId);
        }
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponse> getFile(@PathVariable Integer id) {
        FileResponse file = fileService.findById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(file);
    }

    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(
            @RequestParam Integer userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer folderId) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .build();
        }

        FileResponse uploadedFile = fileService.upload(userId, file, folderId);
        return ResponseEntity.ok(uploadedFile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileResponse> updateFile(
            @PathVariable Integer id,
            @RequestBody FileRequest request) {
        FileResponse updatedFile = fileService.updateDisplayName(id, request.getDisplayName());
        
        if (request.getFolderId() != null) {
            updatedFile = fileService.moveToFolder(id, request.getFolderId());
        }
        
        return ResponseEntity.ok(updatedFile);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) throws IOException {
        FileResponse file = fileService.findById(id);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = fileService.downloadFile(id);
        String filename = file.getDisplayName() != null && !file.getDisplayName().isEmpty() 
                ? file.getDisplayName() 
                : file.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id) {
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-filename")
    public ResponseEntity<Void> deleteFileByFilename(
            @RequestParam Integer userId,
            @RequestParam String filename) {
        FileResponse file = fileService.findByUserIdAndFilePath(userId, filename);
        if (file != null) {
            fileService.delete(file.getId());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
