package org.unimate.unimate.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.NewsPiece;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsPieceRepository extends CrudRepository<NewsPiece, Integer> {
    @Query("SELECT n FROM NewsPiece n LEFT JOIN FETCH n.translations ORDER BY n.publishDate DESC")
    List<NewsPiece> findAll();
    @Query("SELECT n FROM NewsPiece n LEFT JOIN FETCH n.translations ORDER BY n.publishDate DESC")
    List<NewsPiece> findLatest(Pageable pageable);
    @Query("SELECT n FROM NewsPiece n LEFT JOIN FETCH n.translations WHERE n.id = :id")
    Optional<NewsPiece> findById(@Param("id") Integer id);
}
