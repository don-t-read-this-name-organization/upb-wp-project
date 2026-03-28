package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.FacultyLink;

import java.util.List;

@Repository
public interface FacultyLinkRepository extends JpaRepository<FacultyLink, Integer> {
    List<FacultyLink> findByFacultyId(Integer facultyId);
}
