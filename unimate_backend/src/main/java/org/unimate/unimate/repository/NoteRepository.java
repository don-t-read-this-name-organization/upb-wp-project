package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Note;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.contents WHERE n.user.id = :userId AND n.active = true ORDER BY n.createdAt DESC")
    List<Note> findByUserIdAndActiveTrueOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT n FROM Note n LEFT JOIN FETCH n.contents WHERE n.id = :id")
    Optional<Note> findByIdWithContents(Integer id);
}
