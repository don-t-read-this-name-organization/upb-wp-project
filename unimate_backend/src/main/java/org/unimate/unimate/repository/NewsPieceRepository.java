package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.NewsPiece;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsPieceRepository extends CrudRepository<NewsPiece, Integer> {
    @Query("SELECT n FROM NewsPiece n ORDER BY n.publishDate DESC")
    List<NewsPiece> findAll();

    @Query("SELECT n FROM NewsPiece n ORDER BY n.publishDate DESC")
    List<NewsPiece> findLatest(@Param("limit") int limit);

    @Query("SELECT n FROM NewsPiece n WHERE n.id = :id")
    Optional<NewsPiece> findById(@Param("id") Integer id);
}
