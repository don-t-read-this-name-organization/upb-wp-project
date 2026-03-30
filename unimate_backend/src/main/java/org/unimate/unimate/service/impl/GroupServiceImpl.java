package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.domain.entities.Faculty;
import org.unimate.unimate.domain.entities.Group;
import org.unimate.unimate.repository.FacultyTranslationRepository;
import org.unimate.unimate.repository.GroupRepository;
import org.unimate.unimate.service.GroupService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GroupServiceImpl implements GroupService {

    GroupRepository groupRepository;
    FacultyTranslationRepository facultyTranslationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Group> findById(Integer id) {
        return groupRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findWithTranslation(String language) {
        return groupRepository.findAll().stream()
            .map(group -> applyFacultyTranslation(group, language))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByFacultyId(Integer facultyId) {
        return groupRepository.findByFacultyId(facultyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Group> findByFacultyIdWithTranslation(Integer facultyId, String language) {
        return groupRepository.findByFacultyId(facultyId).stream()
            .map(group -> applyFacultyTranslation(group, language))
            .toList();
    }

    private Group applyFacultyTranslation(Group group, String language) {
        if (language == null || language.isEmpty() || group.getFaculty() == null) {
            return group;
        }

        Faculty faculty = group.getFaculty();
        facultyTranslationRepository.findByFacultyIdAndLanguage(faculty.getId(), language)
            .ifPresent(translation -> {
                faculty.setName(translation.getName());
                if (translation.getShortName() != null) {
                    faculty.setShortName(translation.getShortName());
                }
            });

        return group;
    }
}
