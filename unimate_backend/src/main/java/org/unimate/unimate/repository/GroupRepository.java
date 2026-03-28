package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findByFacultyId(Integer facultyId);
    Optional<Group> findById(Integer id);
}
