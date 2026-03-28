package org.unimate.unimate.service;

import org.unimate.unimate.domain.entities.Faculty;

import java.util.List;
import java.util.Optional;

public interface FacultyService {
    List<Faculty> findAll();
    Optional<Faculty> findById(Integer id);
    Optional<Faculty> findByIdWithTranslation(Integer id, String language);
    List<Faculty> findWithTranslation(String language);
}
