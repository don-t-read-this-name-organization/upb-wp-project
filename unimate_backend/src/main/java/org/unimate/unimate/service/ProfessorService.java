package org.unimate.unimate.service;

import org.unimate.unimate.domain.entities.Professor;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
  List<Professor> findAllActive();

  Optional<Professor> findById(Integer id);

  Professor create(String name, String department, String faculty);

  Professor update(Integer id, String name, String department, String faculty);

  void delete(Integer id);
}
