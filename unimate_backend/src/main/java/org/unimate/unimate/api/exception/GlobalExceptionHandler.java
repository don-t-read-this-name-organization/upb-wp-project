package org.unimate.unimate.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.unimate.unimate.exception.AlreadyExistsException;
import org.unimate.unimate.exception.NotFoundException;
import org.unimate.unimate.exception.ValidationException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.NOT_FOUND, "NOT_FOUND", null);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", null);
  }

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleAlreadyExists(AlreadyExistsException ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.CONFLICT, "ALREADY_EXISTS", null);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.FORBIDDEN, "ACCESS_DENIED", null);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));
    return buildResponse(ex, request, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", fieldErrors);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    return buildResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", null);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
      Exception ex,
      HttpServletRequest request,
      HttpStatus status,
      String code,
      Map<String, String> fieldErrors
  ) {
    if (status.is5xxServerError()) {
      log.error("Error {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
    } else {
      log.warn("Request failed: {} {} - {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
    }

    ErrorResponse response = ErrorResponse.builder()
        .code(code)
        .message(resolveMessage(status, ex))
        .timestamp(Instant.now())
        .path(request.getRequestURI())
        .fieldErrors(fieldErrors)
        .build();
    return ResponseEntity.status(status).body(response);
  }

  private String resolveMessage(HttpStatus status, Exception ex) {
    return status == HttpStatus.INTERNAL_SERVER_ERROR 
        ? "An unexpected error occurred" 
        : ex.getMessage();
  }
}
