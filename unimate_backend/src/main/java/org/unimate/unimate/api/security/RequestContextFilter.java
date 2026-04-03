package org.unimate.unimate.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component("unimateRequestContextFilter")
public class RequestContextFilter extends OncePerRequestFilter {

  public static final String REQUEST_ID_KEY = "requestId";
  public static final String USER_ID_KEY = "userId";
  public static final String TIMESTAMP_KEY = "timestamp";
  public static final String REQUEST_ID_HEADER = "X-Request-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestId = request.getHeader(REQUEST_ID_HEADER);
    if (requestId == null || requestId.isBlank()) {
      requestId = UUID.randomUUID().toString();
    }

    request.setAttribute(REQUEST_ID_KEY, requestId);
    response.setHeader(REQUEST_ID_HEADER, requestId);

    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      log.error("Request {} failed: {}", requestId, ex.getMessage(), ex);
      throw ex;
    }
  }
}
