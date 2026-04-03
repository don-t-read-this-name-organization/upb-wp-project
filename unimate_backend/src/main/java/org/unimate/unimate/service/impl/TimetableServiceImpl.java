package org.unimate.unimate.service.impl;

import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
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
      @Value("${upload.timetable-path:./uploads/timetables/}") String timetableUploadPath
  ) {
    this.timetableRepository = timetableRepository;
    this.userRepository = userRepository;
    this.timetableUploadPath = timetableUploadPath;
  }

  @Override
  @Transactional
  public TimetableResponse uploadTimetable(Integer userId, MultipartFile file) {
    validateFile(file);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User", userId));

    timetableRepository.findByUserIdAndActive(userId, true).ifPresent(existing -> {
      existing.setActive(false);
      timetableRepository.save(existing);
      deletePhysicalFile(existing.getFilePath());
    });

    try {
      Path userDir = Paths.get(timetableUploadPath, userId.toString());
      if (!Files.exists(userDir)) {
        Files.createDirectories(userDir);
      }

      String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "timetable.pdf";
      String storedFilename = UUID.randomUUID() + ".pdf";
      Path absoluteFilePath = userDir.resolve(storedFilename);
      Files.copy(file.getInputStream(), absoluteFilePath);

      Timetable timetable = Timetable.builder()
          .user(user)
          .filename(originalFilename)
          .filePath(absoluteFilePath.toString())
          .active(true)
          .build();

      Timetable saved = timetableRepository.save(timetable);
      return TimetableResponse.fromEntity(saved);
    } catch (IOException ex) {
      throw new ValidationException("Failed to upload timetable");
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Resource getTimetable(Integer userId) throws IOException {
    Timetable timetable = timetableRepository.findByUserIdAndActive(userId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "userId", userId));

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
    Timetable timetable = timetableRepository.findByUserIdAndActive(userId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "userId", userId));
    return TimetableResponse.fromEntity(timetable);
  }

  @Override
  @Transactional
  public void deleteTimetable(Integer userId) {
    Timetable timetable = timetableRepository.findByUserIdAndActive(userId, true)
        .orElseThrow(() -> new NotFoundException("Timetable", "userId", userId));

    timetable.setActive(false);
    timetableRepository.save(timetable);
    deletePhysicalFile(timetable.getFilePath());
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

  private void deletePhysicalFile(String absolutePath) {
    try {
      if (absolutePath != null && !absolutePath.isBlank()) {
        Files.deleteIfExists(Paths.get(absolutePath));
      }
    } catch (IOException ignored) {
      // Physical cleanup failure should not block metadata update.
    }
  }
}
