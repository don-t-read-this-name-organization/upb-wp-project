package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.FacultyLinkTranslation;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyLinkTranslationRepository extends JpaRepository<FacultyLinkTranslation, Integer> {
    Optional<FacultyLinkTranslation> findByFacultyLinkIdAndLanguage(Integer facultyLinkId, String language);
}
