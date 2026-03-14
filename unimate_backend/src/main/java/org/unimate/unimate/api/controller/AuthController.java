package org.unimate.unimate.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.unimate.unimate.api.dto.user.response.UserResponse;
import org.unimate.unimate.domain.entities.User;
import org.unimate.unimate.service.UserService;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        return userService.findByEmail(email)
            .filter(user -> {
                String storedHash = user.getPasswordHash();
                if (storedHash != null && storedHash.startsWith("$2")) {
                    return passwordEncoder.matches(password, storedHash);
                }
                return storedHash != null && storedHash.equals(password);
            })
            .map(user -> ResponseEntity.ok(Map.of(
                "user", UserResponse.fromEntity(user),
                "token", "demo-token-" + user.getId()
            )))
            .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}
