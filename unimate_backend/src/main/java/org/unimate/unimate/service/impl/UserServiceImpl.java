package org.unimate.unimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.domain.entities.Faculty;
import org.unimate.unimate.domain.entities.Group;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.exception.AlreadyExistsException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.FacultyRepository;
import org.unimate.unimate.repository.GroupRepository;
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
  FacultyRepository facultyRepository;
  GroupRepository groupRepository;
  PasswordEncoder passwordEncoder;

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  private Faculty getFacultyFromRequest(UserRequest request) {
    if (request.getFacultyId() == null) {
      return null;
    }
    return facultyRepository.findById(request.getFacultyId())
        .orElseThrow(() -> new ValidationException("Faculty not found with id: " + request.getFacultyId()));
  }

  private Group getGroupFromRequest(UserRequest request) {
    if (request.getGroupId() == null) {
      return null;
    }
    return groupRepository.findById(request.getGroupId())
        .orElseThrow(() -> new ValidationException("Group not found with id: " + request.getGroupId()));
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

    Faculty faculty = getFacultyFromRequest(request);
    Group group = getGroupFromRequest(request);

    User user = User.create(request, passwordEncoder.encode(request.getPassword()));
    user.setFaculty(faculty);
    user.setStudyGroup(group);

    final User savedUser = save(user);

    log.info("User created: {}", savedUser.getEmail());
    return UserResponse.fromEntity(savedUser);
  }

  @Transactional
  @Override
  public UserResponse update(User user, UserRequest request) {
    if (request.getRole() == RoleName.ADMIN && user.getRole() != RoleName.ADMIN) {
      throw new ValidationException("An admin already exists. Only one admin is allowed.");
    }

    Faculty faculty = getFacultyFromRequest(request);
    Group group = getGroupFromRequest(request);

    user.update(request, faculty, group);
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
  @Transactional(readOnly = true)
  public Optional<Integer> findIdByEmail(String email) {
    return userRepository.findIdByEmail(email);
  }


  @Override
  @Transactional(readOnly = true)
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

}
