package com.example.board_api.user.service;

import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto signup(UserRequestDto request) {
        // 이메일 중복 검사
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//
//        }
        // 비밀번호 암호화
        // user entity 생성
        User user = new User(request.getEmail(), request.getPassword(),
                            request.getNickname(), request.getProfileImage());
        return new UserResponseDto(user);
    }
}
