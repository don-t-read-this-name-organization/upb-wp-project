package org.unimate.unimate.service;

import org.unimate.unimate.api.dto.faculty.FacultyLinkWithTranslation;

import java.util.List;

public interface FacultyLinkService {
    List<FacultyLinkWithTranslation> findByFacultyIdWithTranslation(Integer facultyId, String language);
}
