error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/User.java:_empty_/ToString#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/User.java
empty definition using pc, found symbol in pc: _empty_/ToString#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 673
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/User.java
text:
```scala
package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.domain.enums.RoleName;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@@@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uq_users_email", columnNames = {"email"}),
    })
@FieldDefaults(level = PRIVATE)
@Builder
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(name = "first_name", length = 100)
  private String firstName;

  @Column(name = "last_name", length = 100)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private RoleName role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "faculty_id")
  @ToString.Exclude
  private Faculty faculty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  @ToString.Exclude
  private Group studyGroup;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Builder.Default
  @Column(name = "login_attempts", nullable = false)
  private Integer loginAttempts = 0;

  @Column(name = "locked_until")
  private LocalDateTime lockedUntil;

  @Builder.Default
  @Column(name = "password_expired", nullable = false, columnDefinition = "TINYINT(1)")
  private Boolean passwordExpired = false;

  @Column(name = "password_changed_at")
  private LocalDateTime passwordChangedAt;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;


  public static User create(UserRequest request, String password) {
    return User.builder()
        .email(request.getEmail())
        .username(request.getUsername())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .passwordHash(password)
        .role(request.getRole())
        .build();
  }

  public void update(UserRequest request, Faculty faculty, Group group) {
    username = request.getUsername();
    firstName = request.getFirstName();
    lastName = request.getLastName();
    role = request.getRole();
    email = request.getEmail();
    this.faculty = faculty;
    this.studyGroup = group;
  }

  public void delete() {
    this.active = false;
  }

  // UserDetails implementation
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return active;
  }

  @Override
  public boolean isAccountNonLocked() {
    return active && (lockedUntil == null || lockedUntil.isBefore(LocalDateTime.now()));
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return !passwordExpired;
  }

  @Override
  public boolean isEnabled() {
    return active;
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/ToString#