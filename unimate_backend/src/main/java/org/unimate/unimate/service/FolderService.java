package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.folder.response.FolderResponse;
import org.unimate.unimate.api.dto.folder.request.FolderRequest;

import java.util.List;

public interface FolderService {
    List<FolderResponse> findByUserId(Integer userId);
    List<FolderResponse> findByUserIdAndParentId(Integer userId, Integer parentId);
    FolderResponse findById(Integer id, Integer userId);
    FolderResponse create(Integer userId, FolderRequest request);
    FolderResponse update(Integer id, Integer userId, FolderRequest request);
    void delete(Integer id, Integer userId);
}
