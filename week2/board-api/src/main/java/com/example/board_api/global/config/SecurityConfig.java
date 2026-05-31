package com.example.board_api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Postman 테스트를 위해 CSRF를 임시로 비활성화
                .csrf(csrf -> csrf.disable())

                // HTTP 인증 필요 여부 분리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users", "/images/**", "/error").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}