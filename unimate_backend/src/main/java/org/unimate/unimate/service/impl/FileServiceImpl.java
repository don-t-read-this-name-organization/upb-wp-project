package org.unimate.unimate.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.unimate.unimate.api.dto.file.response.FileResponse;
import org.unimate.unimate.domain.entities.File;
import org.unimate.unimate.domain.entities.Folder;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.repository.FileRepository;
import org.unimate.unimate.repository.FolderRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final String uploadPath;

    public FileServiceImpl(
            FileRepository fileRepository,
            FolderRepository folderRepository,
            UserRepository userRepository,
            @Value("${app.upload.path}") String uploadPath) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.uploadPath = uploadPath;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponse> findByUserId(Integer userId) {
        return fileRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId).stream()
                .map(FileResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponse> findByUserIdAndFolderId(Integer userId, Integer folderId) {
        List<File> files;
        if (folderId == null) {
            files = fileRepository.findByUserIdAndActiveTrueAndFolderIsNullOrderByCreatedAtDesc(userId);
        } else {
            files = fileRepository.findByUserIdAndActiveTrueAndFolderIdOrderByCreatedAtDesc(userId, folderId);
        }
        return files.stream()
                .map(FileResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponse> findByUserIdAndFileType(Integer userId, String fileType) {
        return fileRepository.findByUserIdAndActiveTrueAndFileTypeContainingIgnoreCaseOrderByCreatedAtDesc(userId, fileType).stream()
                .map(FileResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse findById(Integer id) {
        return fileRepository.findById(id)
                .map(FileResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse findByUserIdAndFilename(Integer userId, String filename) {
        File file = fileRepository.findByUserIdAndFilename(userId, filename);
        return file != null ? FileResponse.fromEntity(file) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponse findByUserIdAndFilePath(Integer userId, String filename) {
        File file = fileRepository.findByUserIdAndFilePathContaining(userId, filename);
        return file != null ? FileResponse.fromEntity(file) : null;
    }

    @Override
    @Transactional
    public FileResponse upload(Integer userId, MultipartFile file, Integer folderId) {
        User user = userRepository.findById(userId).orElseThrow();

        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findByIdAndUserIdAndActiveTrue(folderId, userId).orElse(null);
        }

        try {
            Path userDir = Paths.get(uploadPath, userId.toString());
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            Path filePath = userDir.resolve(storedFilename);
            Files.copy(file.getInputStream(), filePath);

            String fileType = extension.isEmpty() ? "" : extension.substring(1).toLowerCase();
            String relativePath = "/uploads/" + userId + "/" + storedFilename;

            File fileEntity = File.builder()
                    .user(user)
                    .filename(originalFilename)
                    .filePath(relativePath)
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .folder(folder)
                    .active(true)
                    .build();

            fileEntity = fileRepository.save(fileEntity);
            return FileResponse.fromEntity(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public FileResponse updateDisplayName(Integer id, String displayName) {
        File file = fileRepository.findById(id).orElseThrow();
        file.setDisplayName(displayName);
        file = fileRepository.save(file);
        return FileResponse.fromEntity(file);
    }

    @Override
    @Transactional
    public FileResponse moveToFolder(Integer id, Integer folderId) {
        File file = fileRepository.findById(id).orElseThrow();
        
        Folder folder = null;
        if (folderId != null) {
            folder = folderRepository.findByIdAndUserIdAndActiveTrue(folderId, file.getUser().getId()).orElse(null);
        }
        
        file.setFolder(folder);
        file = fileRepository.save(file);
        return FileResponse.fromEntity(file);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(Integer id) throws IOException {
        File file = fileRepository.findById(id).orElseThrow();
        String displayName = file.getDisplayName() != null && !file.getDisplayName().isEmpty() 
                ? file.getDisplayName() 
                : file.getFilename();
        Path filePath = Paths.get(uploadPath, file.getUser().getId().toString())
                .resolve(file.getFilePath().substring(file.getFilePath().lastIndexOf('/') + 1));
        
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Could not read file: " + displayName);
        }
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        fileRepository.findById(id).ifPresent(file -> {
            try {
                String storedFilename = file.getFilePath().substring(file.getFilePath().lastIndexOf('/') + 1);
                Path filePath = Paths.get(uploadPath, file.getUser().getId().toString(), storedFilename);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file from disk: " + e.getMessage());
            }
            file.setActive(false);
            fileRepository.save(file);
        });
    }
}
