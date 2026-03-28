package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.NoteContent;

import java.util.List;

@Repository
public interface NoteContentRepository extends JpaRepository<NoteContent, Integer> {
    List<NoteContent> findByNoteIdOrderBySortOrder(Integer noteId);
    void deleteByNoteId(Integer noteId);
}
