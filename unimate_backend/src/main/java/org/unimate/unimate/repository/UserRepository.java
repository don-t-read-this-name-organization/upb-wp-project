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
  Optional<User> findOneByEmailAndActiveTrue(String email);

  @Query(value = "select * from users where active=true order by id desc", nativeQuery = true)
  List<User> findAll();

  @Query(value = "select * from users where id= :id and active=true", nativeQuery = true)
  Optional<User> findById(@Param("id") Integer id);

  @Query(value = "select id from users where email= :email and active=true", nativeQuery = true)
  Optional<Integer> findIdByEmail(@Param("email") String email);

  @Query(value = "select * from users where email = :email", nativeQuery = true)
  Optional<User> findByEmail(@Param("email") String email);
}
