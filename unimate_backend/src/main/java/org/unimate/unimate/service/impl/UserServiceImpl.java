package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.exception.AlreadyExistsException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.UserService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

  UserRepository userRepository;
  PasswordEncoder passwordEncoder;

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional
  @Override
  public UserResponse create(UserRequest request) {
    String email = request.getEmail();
    if (userRepository.findIdByEmail(email).isPresent()) {
      throw new AlreadyExistsException("User", email);
    }
    if (request.getRole() == RoleName.ADMIN) {
      throw new ValidationException("An admin already exists. Only one admin is allowed.");
    }
    final User user =
          save(User.create(request, passwordEncoder.encode(request.getPassword())));

    log.info("User created: {}", user.getEmail());
    return UserResponse.fromEntity(user);
  }

  @Transactional
  @Override
  public UserResponse update(User user, UserRequest request) {
    if (request.getRole() == RoleName.ADMIN && user.getRole() != RoleName.ADMIN) {
      throw new ValidationException("An admin already exists. Only one admin is allowed.");
    }
    user.update(request);
    User updatedUser = save(user);
    log.info("User updated: {}", user.getEmail());
    return UserResponse.fromEntity(updatedUser);
  }

  @Override
  public void delete(User user) {
    user.delete();
    save(user);
  }

  @Override
  public Optional<Integer> findIdByEmail(String email) {
    return userRepository.findIdByEmail(email);
  }


  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

}
