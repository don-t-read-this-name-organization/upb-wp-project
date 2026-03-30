package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Note;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Integer userId);
    // demesup, you are free to change it
    List<Note> findByUserIdAndActiveTrueAndTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            Integer userId, String title, String content);
}
