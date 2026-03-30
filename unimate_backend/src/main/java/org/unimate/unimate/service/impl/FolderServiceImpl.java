package org.unimate.unimate.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.folder.FolderRequest;
import org.unimate.unimate.api.dto.folder.FolderResponse;
import org.unimate.unimate.domain.entities.Folder;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.repository.FolderRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.FolderService;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderServiceImpl(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> findByUserId(Integer userId) {
        return folderRepository.findByUserIdAndActiveTrueOrderByNameAsc(userId).stream()
                .map(FolderResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> findByUserIdAndParentId(Integer userId, Integer parentId) {
        List<Folder> folders;
        if (parentId == null) {
            folders = folderRepository.findByUserIdAndParentIsNullAndActiveTrueOrderByNameAsc(userId);
        } else {
            folders = folderRepository.findByUserIdAndParentIdAndActiveTrueOrderByNameAsc(userId, parentId);
        }
        return folders.stream()
                .map(FolderResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse findById(Integer id, Integer userId) {
        return folderRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                .map(FolderResponse::fromEntity)
                .orElse(null);
    }

    @Override
    @Transactional
    public FolderResponse create(Integer userId, FolderRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        
        Folder parent = null;
        if (request.getParentId() != null) {
            parent = folderRepository.findByIdAndUserIdAndActiveTrue(request.getParentId(), userId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
        }

        Folder folder = Folder.builder()
                .user(user)
                .name(request.getName())
                .parent(parent)
                .active(true)
                .build();

        folder = folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }

    @Override
    @Transactional
    public FolderResponse update(Integer id, Integer userId, FolderRequest request) {
        Folder folder = folderRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));

        if (request.getName() != null && !request.getName().isEmpty()) {
            folder.setName(request.getName());
        }

        if (request.getParentId() != null) {
            Folder parent = folderRepository.findByIdAndUserIdAndActiveTrue(request.getParentId(), userId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            folder.setParent(parent);
        }

        folder = folderRepository.save(folder);
        return FolderResponse.fromEntity(folder);
    }

    @Override
    @Transactional
    public void delete(Integer id, Integer userId) {
        Folder folder = folderRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                .orElseThrow(() -> new RuntimeException("Folder not found"));
        
        folder.setActive(false);
        folderRepository.save(folder);
    }
}
