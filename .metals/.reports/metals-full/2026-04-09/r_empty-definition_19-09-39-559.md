error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/service/impl/TimetableServiceImpl.java:_empty_/TimetableRepository#findByFacultyIdAndActive#map#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/service/impl/TimetableServiceImpl.java
empty definition using pc, found symbol in pc: _empty_/TimetableRepository#findByFacultyIdAndActive#map#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2732
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/service/impl/TimetableServiceImpl.java
text:
```scala
package org.unimate.unimate.service.impl;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.unimate.unimate.api.dto.timetable.response.TimetableResponse;
import org.unimate.unimate.domain.entities.Timetable;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;
import org.unimate.unimate.repository.TimetableRepository;
import org.unimate.unimate.repository.UserRepository;
import org.unimate.unimate.service.TimetableService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE)
public class TimetableServiceImpl implements TimetableService {

  private static final long MAX_TIMETABLE_SIZE_BYTES = 10L * 1024L * 1024L;

  final TimetableRepository timetableRepository;
  final UserRepository userRepository;

  String timetableUploadPath;

  public TimetableServiceImpl(
      TimetableRepository timetableRepository,
      UserRepository userRepository,
      @Value("${app.upload.timetable-path:${app.upload.path:./uploads/timetables/}}") String timetableUploadPath
  ) {
    this.timetableRepository = timetableRepository;
    this.userRepository = userRepository;
    this.timetableUploadPath = timetableUploadPath;
  }

  /**
   * Get faculty ID from user ID
   */
  private Integer getFacultyIdFromUser(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User", userId));
    
    if (user.getFaculty() == null) {
      throw new ValidationException("User is not assigned to a faculty");
    }
    
    return user.getFaculty().getId();
  }

  @Override
  @Transactional
  public TimetableResponse uploadTimetable(Integer userId, MultipartFile file) {
    validateFile(file);

    Integer facultyId = getFacultyIdFromUser(userId);

    // Deactivate previous timetable for this faculty
    String previousFilePath = timetableRepository.findByFacultyIdAndActive(facultyId, true)
        .@@map(existing -> {
          existing.setActive(false);
          timetableRepository.save(existing);
          return existing.getFilePath();
        })
        .orElse(null);

    Path absoluteFilePath = null;

    try {
      // Create directory based on faculty ID
      Path facultyDir = Paths.get(timetableUploadPath, facultyId.toString());
      if (!Files.exists(facultyDir)) {
        Files.createDirectories(facultyDir);
      }

      String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "timetable.pdf";
      String storedFilename = UUID.randomUUID() + ".pdf";
      absoluteFilePath = facultyDir.resolve(storedFilename);
      Files.copy(file.getInputStream(), absoluteFilePath);

      Timetable timetable = Timetable.builder()
          .faculty(userRepository.findById(userId).orElseThrow().getFaculty())
          .filename(originalFilename)
          .filePath(absoluteFilePath.toString())
          .active(true)
          .build();

      Timetable saved = timetableRepository.save(timetable);
      log.info("Timetable uploaded for faculty {}: {}", facultyId, originalFilename);
      
      if (previousFilePath != null && !previousFilePath.isBlank()) {
        registerDeleteAfterCommit(previousFilePath);
      }
      return TimetableResponse.fromEntity(saved);
    } catch (IOException ex) {
      deletePhysicalFile(absoluteFilePath);
      throw new ValidationException("Failed to upload timetable");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Resource getTimetable(Integer userId) throws IOException {
    Integer facultyId = getFacultyIdFromUser(userId);
    
    Timetable timetable = timetableRepository.findByFacultyIdAndActive(facultyId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "facultyId", facultyId));

    Path path = Paths.get(timetable.getFilePath());
    Resource resource = new UrlResource(path.toUri());
    if (!resource.exists() || !resource.isReadable()) {
      throw new IOException("Timetable file not found or unreadable");
    }
    return resource;
  }

  @Override
  @Transactional(readOnly = true)
  public TimetableResponse getTimetableMetadata(Integer userId) {
    Integer facultyId = getFacultyIdFromUser(userId);
    
    Timetable timetable = timetableRepository.findByFacultyIdAndActive(facultyId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "facultyId", facultyId));
    return TimetableResponse.fromEntity(timetable);
  }

  @Override
  @Transactional
  public void deleteTimetable(Integer userId) {
    Integer facultyId = getFacultyIdFromUser(userId);
    
    Timetable timetable = timetableRepository.findByFacultyIdAndActive(facultyId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "facultyId", facultyId));

    timetable.setActive(false);
    timetableRepository.save(timetable);
    deletePhysicalFile(timetable.getFilePath());
    log.info("Timetable deleted for faculty {}", facultyId);
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ValidationException("Timetable file is required");
    }
    if (file.getSize() > MAX_TIMETABLE_SIZE_BYTES) {
      throw new ValidationException("Timetable exceeds max size of 10MB");
    }

    String fileName = file.getOriginalFilename();
    String contentType = file.getContentType();
    boolean isPdfByName = fileName != null && fileName.toLowerCase().endsWith(".pdf");
    boolean isPdfByMime = "application/pdf".equalsIgnoreCase(contentType);
    if (!isPdfByName && !isPdfByMime) {
      throw new ValidationException("Only PDF timetable files are allowed");
    }
  }

  private void deletePhysicalFile(Path absolutePath) {
    if (absolutePath == null) {
      return;
    }
    deletePhysicalFile(absolutePath.toString());
  }

  private void deletePhysicalFile(String absolutePath) {
    try {
      if (absolutePath != null && !absolutePath.isBlank()) {
        Files.deleteIfExists(Paths.get(absolutePath));
      }
    } catch (IOException ignored) {
      // Physical cleanup failure should not block metadata update.
    }
  }

  private void registerDeleteAfterCommit(String absolutePath) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      deletePhysicalFile(absolutePath);
      return;
    }

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        deletePhysicalFile(absolutePath);
      }
    });
  }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/TimetableRepository#findByFacultyIdAndActive#map#