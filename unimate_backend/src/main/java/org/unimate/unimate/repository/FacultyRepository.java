package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Faculty;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    Optional<Faculty> findById(Integer id);
}
