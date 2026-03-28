package org.unimate.unimate.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // It needs a remake, it becomes a mess...
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-ui/index.html"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/tasks/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/tasks/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/tasks/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/tasks/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/subtasks/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/subtasks/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/subtasks/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/subtasks/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/quotes/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/quotes/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/quotes/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/quotes/**").permitAll()
                    // It should be maintained only by an admin
                    .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/news/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/news/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/news/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
