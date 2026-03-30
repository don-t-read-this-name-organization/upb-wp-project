package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Folder;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer> {
    List<Folder> findByUserIdAndActiveTrueOrderByNameAsc(Integer userId);
    
    List<Folder> findByUserIdAndParentIdAndActiveTrueOrderByNameAsc(Integer userId, Integer parentId);
    
    List<Folder> findByUserIdAndParentIsNullAndActiveTrueOrderByNameAsc(Integer userId);
    
    Optional<Folder> findByIdAndUserIdAndActiveTrue(Integer id, Integer userId);
}
