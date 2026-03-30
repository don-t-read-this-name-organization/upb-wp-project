package org.unimate.unimate.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.unimate.unimate.domain.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.faculty LEFT JOIN FETCH u.studyGroup WHERE u.active = true")
  List<User> findAll();

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.faculty LEFT JOIN FETCH u.studyGroup")
  List<User> findAllIncludingInactive();

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.faculty LEFT JOIN FETCH u.studyGroup WHERE u.id = :id AND u.active = true")
  Optional<User> findById(@Param("id") Integer id);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.faculty LEFT JOIN FETCH u.studyGroup WHERE u.id = :id")
  Optional<User> findByIdIncludingInactive(@Param("id") Integer id);

  @Query(value = "select id from users where email= :email and active=true", nativeQuery = true)
  Optional<Integer> findIdByEmail(@Param("email") String email);

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.faculty LEFT JOIN FETCH u.studyGroup WHERE u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  @Query("SELECT u FROM User u WHERE LOWER(u.firstName) = LOWER(:firstName) AND LOWER(u.lastName) = LOWER(:lastName) AND u.active = true AND u.firstName IS NOT NULL AND u.lastName IS NOT NULL")
  List<User> findByFirstNameAndLastNameIgnoreCase(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
