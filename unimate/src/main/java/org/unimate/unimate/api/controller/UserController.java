package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.service.UserService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

  UserService userService;

  @PostMapping
  public UserResponse create(@RequestBody UserRequest request) {
    return userService.create(request);
  }


  // use @Secured({"ROLE_NAME""}) to limit the access
  @GetMapping("/{id}")
  public UserResponse getById(@PathVariable Integer id) {
    return userService.findById(id).map(UserResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("User", id));
  }

  @GetMapping
  public List<UserResponse> list() {
    return userService.findAll().stream().map(UserResponse::fromEntity).toList();
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable Integer id, @RequestBody UserRequest request) {
    User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    return userService.update(user, request);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    userService.delete(user);
  }
}
