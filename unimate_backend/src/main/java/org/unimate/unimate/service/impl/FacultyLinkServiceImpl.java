package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.unimate.unimate.api.dto.faculty.FacultyLinkWithTranslation;
import org.unimate.unimate.domain.entities.FacultyLink;
import org.unimate.unimate.domain.entities.FacultyLinkTranslation;
import org.unimate.unimate.repository.FacultyLinkRepository;
import org.unimate.unimate.repository.FacultyLinkTranslationRepository;
import org.unimate.unimate.service.FacultyLinkService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FacultyLinkServiceImpl implements FacultyLinkService {

    FacultyLinkRepository facultyLinkRepository;
    FacultyLinkTranslationRepository facultyLinkTranslationRepository;

    @Override
    public List<FacultyLinkWithTranslation> findByFacultyIdWithTranslation(Integer facultyId, String language) {
        List<FacultyLink> links = facultyLinkRepository.findByFacultyId(facultyId);
        
        return links.stream()
                .map(link -> {
                    FacultyLinkTranslation translation = facultyLinkTranslationRepository
                            .findByFacultyLinkIdAndLanguage(link.getId(), language)
                            .orElse(null);
                    return FacultyLinkWithTranslation.fromEntity(link, translation);
                })
                .toList();
    }
}
