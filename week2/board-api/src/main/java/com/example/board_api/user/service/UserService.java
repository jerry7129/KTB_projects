package com.example.board_api.user.service;

import com.example.board_api.exception.BusinessException;
import com.example.board_api.user.UserRole;
import com.example.board_api.user.UserStatus;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(@Qualifier("jdbcRepo") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto signup(UserRequestDto request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("USER_EMAIL_CONFLICT", "중복된 이메일 입니다.", HttpStatus.CONFLICT);
        } else if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        //비밀번호 암호화

        // 그냥 Service에서 timestamp를 계산한 뒤 새로운 User 객체에 값을 저장해 Repository로 전달
        // Instant.now()는 UTC+0으로 고정되어 있음. DB와 timezone을 맞추기 위해서 사용.
        Instant now = Instant.now();
        User user = User.builder().email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .profileImageURL(request.getProfileImageURL())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now).build();
        user = userRepository.save(user);
        return new UserResponseDto(user);
    }
}
