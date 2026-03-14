package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.unimate.unimate.api.dto.user.request.UserRequest;
import org.unimate.unimate.domain.enums.RoleName;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uq_users_email", columnNames = {"email"}),
    })
@FieldDefaults(level = PRIVATE)
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 50)
  private String username;

  @Column(nullable = false)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private RoleName role;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean active = true;


  public static User create(UserRequest request, String password) {
    return User.builder()
        .email(request.getEmail())
        .username(request.getUsername())
        .passwordHash(password)
        .role(request.getRole())
        .build();
  }

  public void update(UserRequest request) {
    username = request.getUsername();
    role = request.getRole();
    email = request.getEmail();
  }

  public void delete() {
    this.active = false;
  }
}
