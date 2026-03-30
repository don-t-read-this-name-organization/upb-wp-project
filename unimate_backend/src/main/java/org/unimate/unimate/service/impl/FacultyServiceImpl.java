package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.domain.entities.Faculty;
import org.unimate.unimate.domain.entities.FacultyTranslation;
import org.unimate.unimate.repository.FacultyRepository;
import org.unimate.unimate.repository.FacultyTranslationRepository;
import org.unimate.unimate.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FacultyServiceImpl implements FacultyService {

    FacultyRepository facultyRepository;
    FacultyTranslationRepository facultyTranslationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Faculty> findAll() {
        return facultyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Faculty> findById(Integer id) {
        return facultyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Faculty> findByIdWithTranslation(Integer id, String language) {
        return facultyRepository.findById(id)
            .map(faculty -> applyTranslation(faculty, language));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Faculty> findWithTranslation(String language) {
        return facultyRepository.findAll().stream()
            .map(faculty -> applyTranslation(faculty, language))
            .toList();
    }

    private Faculty applyTranslation(Faculty faculty, String language) {
        if (language == null || language.isEmpty()) {
            return faculty;
        }
        
        facultyTranslationRepository.findByFacultyIdAndLanguage(faculty.getId(), language)
            .ifPresent(translation -> {
                faculty.setName(translation.getName());
                if (translation.getShortName() != null) {
                    faculty.setShortName(translation.getShortName());
                }
            });
        
        return faculty;
    }
}
