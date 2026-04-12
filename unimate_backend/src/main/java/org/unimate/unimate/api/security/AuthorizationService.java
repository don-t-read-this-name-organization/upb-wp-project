package org.unimate.unimate.api.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.unimate.unimate.repository.FileRepository;
import org.unimate.unimate.repository.FolderRepository;
import org.unimate.unimate.repository.NoteRepository;
import org.unimate.unimate.repository.ReviewRepository;
import org.unimate.unimate.repository.ScheduleRepository;
import org.unimate.unimate.repository.SubtaskRepository;
import org.unimate.unimate.repository.TaskRepository;

@Component("authorizationService")
@RequiredArgsConstructor
public class AuthorizationService {

  private final TaskRepository taskRepository;
  private final SubtaskRepository subtaskRepository;
  private final NoteRepository noteRepository;
  private final FileRepository fileRepository;
  private final FolderRepository folderRepository;
  private final ReviewRepository reviewRepository;
  private final ScheduleRepository scheduleRepository;

  public boolean isCurrentUser(Integer userId) {
    Integer currentUserId = getCurrentUserId();
    return currentUserId != null && currentUserId.equals(userId);
  }

  public boolean canAccessUser(Integer userId) {
    return isAdmin() || isCurrentUser(userId);
  }

  public boolean ownsTask(Integer taskId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return taskRepository.findById(taskId)
        .filter(task -> Boolean.TRUE.equals(task.getActive()))
        .map(task -> task.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsSubtask(Integer subtaskId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return subtaskRepository.findById(subtaskId)
        .filter(subtask -> Boolean.TRUE.equals(subtask.getTask().getActive()))
        .map(subtask -> subtask.getTask().getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsNote(Integer noteId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return noteRepository.findById(noteId)
        .filter(note -> Boolean.TRUE.equals(note.getActive()))
        .map(note -> note.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsFile(Integer fileId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return fileRepository.findById(fileId)
        .filter(file -> Boolean.TRUE.equals(file.getActive()))
        .map(file -> file.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsFolder(Integer folderId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return folderRepository.findById(folderId)
        .filter(folder -> Boolean.TRUE.equals(folder.getActive()))
        .map(folder -> folder.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsReview(Integer reviewId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return reviewRepository.findById(reviewId)
        .filter(review -> Boolean.TRUE.equals(review.getActive()))
        .map(review -> review.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public boolean ownsSchedule(Integer scheduleId) {
    Integer currentUserId = getCurrentUserId();
    if (currentUserId == null) {
      return false;
    }
    return scheduleRepository.findById(scheduleId)
        .filter(schedule -> Boolean.TRUE.equals(schedule.getActive()))
        .map(schedule -> schedule.getUser().getId().equals(currentUserId))
        .orElse(false);
  }

  public Integer getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthenticatedUser authenticatedUser) {
      return authenticatedUser.getId();
    }
    return null;
  }

  public boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
  }

  public boolean isChief() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_CHIEF".equals(authority.getAuthority()));
  }

  public boolean isStudent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));
  }

  public boolean isChiefOrStudent() {
    return isChief() || isStudent();
  }

  public boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated();
  }
}
