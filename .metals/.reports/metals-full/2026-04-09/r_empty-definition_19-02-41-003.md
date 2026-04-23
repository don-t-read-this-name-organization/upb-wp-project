error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtAuthenticationFilter.java:java/lang/Boolean#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtAuthenticationFilter.java
empty definition using pc, found symbol in pc: java/lang/Boolean#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2190
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/api/security/JwtAuthenticationFilter.java
text:
```scala
package org.unimate.unimate.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.repository.UserRepository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractTokenFromRequest(request);

      if (token != null && jwtTokenProvider.validateToken(token)) {
        Integer userId = jwtTokenProvider.getUserIdFromToken(token);
        
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          Optional<User> userOptional = userRepository.findById(userId);
          
          if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (@@Boolean.TRUE.equals(user.getActive())) {
              setupSecurityContext(user, request);
              MDC.put(RequestContextFilter.USER_ID_KEY, String.valueOf(user.getId()));
              log.debug("User {} authenticated via JWT", user.getEmail());
            } else {
              log.warn("User {} is inactive", user.getEmail());
            }
          } else {
            log.warn("User not found for ID: {}", userId);
          }
        }
      }
    } catch (Exception ex) {
      log.error("Error processing JWT authentication: {}", ex.getMessage(), ex);
      // Continue filter chain even if JWT processing fails
    }

    filterChain.doFilter(request, response);
  }

  private void setupSecurityContext(User user, HttpServletRequest request) {
    Collection<? extends GrantedAuthority> authorities = getAuthorities(user.getRole());
    AuthenticatedUser principal = new AuthenticatedUser(
        user.getId(),
        user.getEmail(),
        user.getRole(),
        authorities
    );

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        principal,
        null,
        authorities
    );
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private Collection<? extends GrantedAuthority> getAuthorities(RoleName roleName) {
    List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + roleName.name())
    );
    return authorities;
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    
    if (authHeader == null || authHeader.isEmpty()) {
      return null;
    }
    
    if (!authHeader.startsWith(TOKEN_PREFIX)) {
      log.debug("Authorization header does not start with Bearer prefix");
      return null;
    }
    
    String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
    
    if (token.isEmpty()) {
      log.debug("Token is empty after prefix removal");
      return null;
    }
    
    return token;
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/Boolean#