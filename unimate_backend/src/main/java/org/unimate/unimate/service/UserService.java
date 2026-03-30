package org.unimate.unimate.service;

import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.domain.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

  List<User> findAll();

  Optional<User> findById(Integer id);

  User save(User user);

  @Transactional
  UserResponse create(UserRequest request);

  @Transactional
  UserResponse createPending(UserRequest request);

  @Transactional(readOnly = true)
  List<User> findPendingUsers();

  @Transactional
  UserResponse approveUser(Integer id);

  @Transactional
  void rejectUser(Integer id);

  @Transactional
  UserResponse update(User user, UserRequest request);

  void delete(User user);

  Optional<Integer> findIdByEmail(String email);

  Optional<User> findByEmail(String email);

  @Transactional
  void changePassword(Integer userId, String oldPassword, String newPassword);
}
