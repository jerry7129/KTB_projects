package com.example.board_api.global.config;

import com.example.board_api.user.domain.UserRepository;
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
        if (userRepository.count() >= 10) return;
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String password = "12341234aS!" + i;
            User user = User.builder().nickname("tester" + i)
                            .email("tester" + i + "@adapterz.kr")
                            .password(password)
                            .build();
            userRepository.save(user);
        });
    }
}
