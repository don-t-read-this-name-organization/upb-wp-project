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

  private User requireCurrentUserEntity(AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }
    return userService.findById(currentUser.getId())
        .orElseThrow(() -> new ValidationException("User is not authenticated"));
  }

  @PostMapping("/register")
  public UserResponse register(@RequestBody UserRequest request) {
    return userService.createPending(request);
  }

  @PostMapping
  @Secured({"ROLE_ADMIN", "ROLE_SUPERADMIN"})
  public UserResponse create(@RequestBody UserRequest request, @AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }

    if (currentUser.getRole() == RoleName.ADMIN) {
      User admin = requireCurrentUserEntity(currentUser);
      if (admin.getFaculty() == null) {
        throw new ValidationException("Admin must be assigned to a faculty");
      }

      request.setRole(RoleName.STUDENT);
      request.setFacultyId(admin.getFaculty().getId());
    }
    return userService.create(request);
  }


  @GetMapping("/{id}")
  @PreAuthorize("@authorizationService.canAccessUser(#id)")
  public UserResponse getById(@PathVariable Integer id) {
    return userService.findByIdIncludingInactive(id).map(UserResponse::fromEntity)
        .orElseThrow(() -> new NotFoundException("User", id));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public List<UserResponse> list(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }

    if (currentUser.getRole() == RoleName.SUPERADMIN) {
      return userService.findAll().stream().map(UserResponse::fromEntity).toList();
    }

    User admin = requireCurrentUserEntity(currentUser);
    if (admin.getFaculty() == null) {
      throw new ValidationException("Admin must be assigned to a faculty");
    }

    return userService.findAll().stream()
        .filter(u -> u.getRole() == RoleName.STUDENT)
        .filter(u -> u.getFaculty() != null && admin.getFaculty().getId().equals(u.getFaculty().getId()))
        .map(UserResponse::fromEntity)
        .toList();
  }

  @PutMapping("/{id}")
  @PreAuthorize("@authorizationService.canAccessUser(#id)")
  public UserResponse update(
      @PathVariable Integer id,
      @RequestBody UserRequest request,
      @AuthenticationPrincipal AuthenticatedUser currentUser
  ) {
    User actor = requireCurrentUserEntity(currentUser);
    boolean isSelfUpdate = actor.getId().equals(id);
    boolean isAdmin = currentUser.getRole() == RoleName.ADMIN;
    boolean isSuperadmin = currentUser.getRole() == RoleName.SUPERADMIN;

    if (!isSelfUpdate && !isAdmin && !isSuperadmin) {
      throw new AccessDeniedException("You can only update your own user");
    }

    User user = userService.findByIdIncludingInactive(id).orElseThrow(() -> new NotFoundException("User", id));

    if (isAdmin && !isSelfUpdate) {
      if (actor.getFaculty() == null) {
        throw new ValidationException("Admin must be assigned to a faculty");
      }
      request.setRole(RoleName.STUDENT);
      request.setFacultyId(actor.getFaculty().getId());
    }

    if (isAdmin && isSelfUpdate) {
      request.setRole(RoleName.ADMIN);
      if (actor.getFaculty() != null) {
        request.setFacultyId(actor.getFaculty().getId());
      }
    }

    if (!isAdmin && !isSuperadmin) {
      request.setRole(user.getRole());
      request.setPassword(null); // Non-admin users cannot change password via this endpoint
    }
    return userService.update(user, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@authorizationService.canManageUser(#id)")
  public void delete(@PathVariable Integer id) {
    User user = userService.findByIdIncludingInactive(id).orElseThrow(() -> new NotFoundException("User", id));
    userService.delete(user);
  }

  @GetMapping("/pending")
  @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
  public List<UserResponse> listPending(@AuthenticationPrincipal AuthenticatedUser currentUser) {
    if (currentUser == null) {
      throw new ValidationException("User is not authenticated");
    }

    List<User> pending = userService.findPendingUsers();

    if (currentUser.getRole() == RoleName.SUPERADMIN) {
      return pending.stream().map(UserResponse::fromEntity).toList();
    }

    User admin = requireCurrentUserEntity(currentUser);
    if (admin.getFaculty() == null) {
      throw new ValidationException("Admin must be assigned to a faculty");
    }

    return pending.stream()
        .filter(u -> u.getRole() == RoleName.STUDENT)
        .filter(u -> u.getFaculty() != null && admin.getFaculty().getId().equals(u.getFaculty().getId()))
        .map(UserResponse::fromEntity)
        .toList();
  }

  @PostMapping("/{id}/approve")
  @PreAuthorize("@authorizationService.canManageUser(#id)")
  public UserResponse approve(@PathVariable Integer id) {
    return userService.approveUser(id);
  }

  @PostMapping("/{id}/reject")
  @PreAuthorize("@authorizationService.canManageUser(#id)")
  public void reject(@PathVariable Integer id) {
    userService.rejectUser(id);
  }

  @PostMapping("/{id}/change-password")
  @PreAuthorize("@authorizationService.canAccessUser(#id)")
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
