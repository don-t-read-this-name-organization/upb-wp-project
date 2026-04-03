package org.unimate.unimate.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.unimate.unimate.api.security.AuthenticatedUser;
import org.unimate.unimate.api.security.RequestContextFilter;
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
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
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
    enrichMdc(request);
    if (status.is5xxServerError()) {
      log.error("Unhandled exception on [{} {}]", request.getMethod(), request.getRequestURI(), ex);
    } else {
      log.warn("Request failed with status {} on [{} {}]: {}", status.value(), request.getMethod(), request.getRequestURI(), ex.getMessage());
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

  private void enrichMdc(HttpServletRequest request) {
    Object requestId = request.getAttribute(RequestContextFilter.REQUEST_ID_KEY);
    if (requestId instanceof String id && !id.isBlank()) {
      MDC.put(RequestContextFilter.REQUEST_ID_KEY, id);
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser) {
      MDC.put(RequestContextFilter.USER_ID_KEY, String.valueOf(authenticatedUser.getId()));
    }
    MDC.put(RequestContextFilter.TIMESTAMP_KEY, Instant.now().toString());
  }

  private String resolveMessage(HttpStatus status, Exception ex) {
    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
      return "An unexpected error occurred";
    }
    return ex.getMessage();
  }
}
