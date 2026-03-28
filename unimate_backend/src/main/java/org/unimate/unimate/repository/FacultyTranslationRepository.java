package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.FacultyTranslation;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyTranslationRepository extends JpaRepository<FacultyTranslation, Integer> {
    Optional<FacultyTranslation> findByFacultyIdAndLanguage(Integer facultyId, String language);
}
