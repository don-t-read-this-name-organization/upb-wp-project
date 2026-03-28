package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Quote;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends CrudRepository<Quote, Integer> {
    @Query(value = "SELECT * FROM quotes WHERE active = true ORDER BY RAND() LIMIT :number", nativeQuery = true)
    List<Quote> findNRandomQuotes(@Param("number") int number);

    @Query(value = "SELECT * FROM quotes WHERE active = true AND id = :id", nativeQuery = true)
    Optional<Quote> findById(@Param("id") Integer id);

    @Query(value = "SELECT * FROM quotes WHERE active = true ORDER BY id desc", nativeQuery = true)
    List<Quote> findAll();

    @Query(value = "SELECT * FROM quotes WHERE active = false ORDER BY id desc", nativeQuery = true)
    List<Quote> findAllPending();

    @Query(value = "SELECT * FROM quotes WHERE active = false AND id = :id", nativeQuery = true)
    Optional<Quote> findPendingById(@Param("id") Integer id);
}
