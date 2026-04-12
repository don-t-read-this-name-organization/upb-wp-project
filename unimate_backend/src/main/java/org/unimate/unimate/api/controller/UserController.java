package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.service.UserService;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

  UserService userService;

  @PostMapping("/register")
  public UserResponse register(@RequestBody UserRequest request) {
    return userService.createPending(request);
  }

  @PostMapping
  @Secured({"ROLE_ADMIN"})
  public UserResponse create(@RequestBody UserRequest request) {
    return userService.create(request);
  }


  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#id)")
  public UserResponse getById(@PathVariable Integer id) {
    return userService.findById(id).map(UserResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("User", id));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> list() {
    return userService.findAll().stream().map(UserResponse::fromEntity).toList();
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#id)")
  public UserResponse update(
      @PathVariable Integer id,
      @RequestBody UserRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }

    boolean isAdmin = currentUser.getRole() == RoleName.ADMIN;
    if (!isAdmin && !currentUser.getId().equals(id)) {
      throw new AccessDeniedException("You can only update your own user");
    }

    User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    if (!isAdmin) {
      request.setRole(user.getRole());
    }
    return userService.update(user, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Integer id) {
    User user = userService.findById(id).orElseThrow(() -> new NotFoundException("User", id));
    userService.delete(user);
  }

  @GetMapping("/pending")
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> listPending() {
    return userService.findPendingUsers().stream().map(UserResponse::fromEntity).toList();
  }

  @PostMapping("/{id}/approve")
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse approve(@PathVariable Integer id) {
    return userService.approveUser(id);
  }

  @PostMapping("/{id}/reject")
  @PreAuthorize("hasRole('ADMIN')")
  public void reject(@PathVariable Integer id) {
    userService.rejectUser(id);
  }

  @PostMapping("/{id}/change-password")
  @PreAuthorize("hasRole('ADMIN') or @authorizationService.isCurrentUser(#id)")
  public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody Map<String, String> request) {
    try {
      String oldPassword = request.get("oldPassword");
      String newPassword = request.get("newPassword");
      userService.changePassword(id, oldPassword, newPassword);
      return ResponseEntity.ok().build();
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (NotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}
