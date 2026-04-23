package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.domain.entities.Professor;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.repository.ProfessorRepository;
import org.unimate.unimate.service.ProfessorService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProfessorServiceImpl implements ProfessorService {

  ProfessorRepository professorRepository;

  @Override
  @Transactional(readOnly = true)
  public List<Professor> findAllActive() {
    return professorRepository.findByActiveTrueOrderByNameAsc();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Professor> findById(Integer id) {
    return professorRepository.findByIdAndActiveTrue(id);
  }

  @Override
  @Transactional
  public Professor create(String name, String department, String faculty, String phone, String email, String officeLocation, String officeHours) {
    Professor professor = Professor.builder()
        .name(name)
        .department(department)
        .faculty(faculty)
        .phone(phone)
        .email(email)
        .officeLocation(officeLocation)
        .officeHours(officeHours)
        .active(true)
        .build();
    return professorRepository.save(professor);
  }

  @Override
  @Transactional
  public Professor update(Integer id, String name, String department, String faculty, String phone, String email, String officeLocation, String officeHours) {
    Professor professor = professorRepository.findByIdAndActiveTrue(id)
        .orElseThrow(() -> new NotFoundException("Professor", id));

    professor.setName(name);
    professor.setDepartment(department);
    professor.setFaculty(faculty);
    professor.setPhone(phone);
    professor.setEmail(email);
    professor.setOfficeLocation(officeLocation);
    professor.setOfficeHours(officeHours);
    return professorRepository.save(professor);
  }

  @Override
  @Transactional
  public void delete(Integer id) {
    Professor professor = professorRepository.findByIdAndActiveTrue(id)
        .orElseThrow(() -> new NotFoundException("Professor", id));
    professor.setActive(false);
    professorRepository.save(professor);
  }
}
