package org.unimate.unimate.api.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.unimate.unimate.domain.enums.RoleName;

import java.security.Principal;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser implements Principal {
  private final Integer id;
  private final String email;
  private final RoleName role;
  private final Collection<? extends GrantedAuthority> authorities;

  @Override
  public String getName() {
    return email;
  }
}
