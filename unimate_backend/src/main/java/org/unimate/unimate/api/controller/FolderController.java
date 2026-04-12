package org.unimate.unimate.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.folder.request.FolderRequest;
import org.unimate.unimate.api.dto.folder.response.FolderResponse;
import org.unimate.unimate.service.FolderService;
import org.unimate.unimate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#userId)")
    public ResponseEntity<List<FolderResponse>> getFolders(
            @RequestParam Integer userId,
            @RequestParam(required = false) Integer parentId) {
        
        List<FolderResponse> folders;
        if (parentId != null) {
            folders = folderService.findByUserIdAndParentId(userId, parentId);
        } else {
            folders = folderService.findByUserIdAndParentId(userId, null);
        }
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (@authorizationService.isCurrentUser(#userId) and @authorizationService.ownsFolder(#id))")
    public ResponseEntity<FolderResponse> getFolder(@PathVariable Integer id, @RequestParam Integer userId) {
        FolderResponse folder = folderService.findById(id, userId);
        if (folder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(folder);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#userId)")
    public ResponseEntity<FolderResponse> createFolder(
            @RequestParam Integer userId,
            @RequestBody FolderRequest request) {
        FolderResponse folder = folderService.create(userId, request);
        return ResponseEntity.ok(folder);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (@authorizationService.isCurrentUser(#userId) and @authorizationService.ownsFolder(#id))")
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable Integer id,
            @RequestParam Integer userId,
            @RequestBody FolderRequest request) {
        FolderResponse folder = folderService.update(id, userId, request);
        return ResponseEntity.ok(folder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (@authorizationService.isCurrentUser(#userId) and @authorizationService.ownsFolder(#id))")
    public ResponseEntity<Void> deleteFolder(@PathVariable Integer id, @RequestParam Integer userId) {
        folderService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
