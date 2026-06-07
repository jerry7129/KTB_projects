package com.example.board_api.global.config;

import com.example.board_api.user.repository.UserRepository;
import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("development")
public class SeedConfig {

    private final UserRepository userRepository;

    public SeedConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    ApplicationRunner sendRunner() {
        return arguments -> seed();
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 0) return;
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String password = "12341234aS!" + i;
            User user = User.builder().nickname("tester" + i)
                            .email("tester" + i + "@adapterz.kr")
                            .password(password)
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .build();
            userRepository.save(user);
        });
    }
}
