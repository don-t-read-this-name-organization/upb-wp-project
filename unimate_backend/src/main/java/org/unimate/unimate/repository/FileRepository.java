package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Integer userId);
    List<File> findByUserIdAndActiveTrueAndFolderIdOrderByCreatedAtDesc(Integer userId, Integer folderId);
    List<File> findByUserIdAndActiveTrueAndFolderIsNullOrderByCreatedAtDesc(Integer userId);
    List<File> findByUserIdAndActiveTrueAndFileTypeContainingIgnoreCaseOrderByCreatedAtDesc(Integer userId, String fileType);
    File findByUserIdAndFilename(Integer userId, String filename);
    @Query("SELECT f FROM File f WHERE f.user.id = :userId AND f.active = true AND f.filePath LIKE %:filename")
    File findByUserIdAndFilePathContaining(@Param("userId") Integer userId, @Param("filename") String filename);
}
