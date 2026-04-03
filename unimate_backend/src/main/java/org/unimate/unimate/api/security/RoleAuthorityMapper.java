package org.unimate.unimate.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.unimate.unimate.domain.enums.RoleName;
import org.unimate.unimate.domain.enums.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RoleAuthorityMapper {

  private RoleAuthorityMapper() {
  }

  public static UserRole toUserRole(RoleName roleName) {
    if (roleName == RoleName.ADMIN) {
      return UserRole.ROLE_ADMIN;
    }
    return UserRole.ROLE_USER;
  }

  public static Collection<? extends GrantedAuthority> toAuthorities(RoleName roleName) {
    UserRole userRole = toUserRole(roleName);
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(userRole.name()));
    if (userRole == UserRole.ROLE_ADMIN) {
      authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_USER.name()));
    }
    return List.copyOf(authorities);
  }
}
