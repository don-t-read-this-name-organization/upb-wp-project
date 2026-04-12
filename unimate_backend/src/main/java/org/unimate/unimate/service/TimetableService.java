package org.unimate.unimate.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import org.unimate.unimate.api.dto.timetable.response.TimetableResponse;

import java.io.IOException;

public interface TimetableService {
  TimetableResponse uploadTimetable(Integer userId, MultipartFile file);

  Resource getTimetable(Integer userId) throws IOException;

  TimetableResponse getTimetableMetadata(Integer userId);

  void deleteTimetable(Integer userId);
}
