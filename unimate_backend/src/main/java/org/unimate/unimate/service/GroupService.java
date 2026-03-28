package org.unimate.unimate.service;

import org.unimate.unimate.domain.entities.Group;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    List<Group> findAll();
    Optional<Group> findById(Integer id);
    List<Group> findWithTranslation(String language);
    List<Group> findByFacultyId(Integer facultyId);
    List<Group> findByFacultyIdWithTranslation(Integer facultyId, String language);
}
