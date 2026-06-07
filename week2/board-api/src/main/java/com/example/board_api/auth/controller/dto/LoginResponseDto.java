package com.example.board_api.auth.controller.dto;

import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.domain.entity.User;
import com.example.board_api.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    // 이렇게 user와 token으로 나누면 Jackson이 JSON으로 변환할 때,
    // { "user": { ... }, "token": { ... } } 형식으로 만듦.
    private LoginUserInfoDto user;
    private TokenInfoDto token;

    public static LoginResponseDto of(
            User user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponseDto(
                new LoginUserInfoDto(
                        user.getId(), user.getEmail(), user.getNickname(),
                        user.getProfileImageUris(), user.getRole(), user.getStatus(),
                        user.getCreatedAt().toString(), user.getUpdatedAt().toString()),
                new TokenInfoDto(accessToken, expiresIn)
        );
    }

    // User 객체 그대로 보내면 password까지 보내야하니까 static inner class를 만들어서
    // http response body에 포함될 내용을 제한함.
    // Dto 파일이 너무 많으면 복잡하기에 이런 간단한 기능은 static class로 구현했다.
    @Getter
    @AllArgsConstructor
    public static class LoginUserInfoDto {
        private Integer userId;
        private String email;
        private String nickname;
        private String profileImageUrl;
        private UserRole role;
        private UserStatus status;
        private String createdAt;
        private String updatedAt;
    }
}
