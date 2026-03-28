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
}
