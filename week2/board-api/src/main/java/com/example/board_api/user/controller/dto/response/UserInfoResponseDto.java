package com.example.board_api.user.controller.dto.response;
import com.example.board_api.global.util.FileUtil;
import com.example.board_api.user.domain.entity.User;

import java.util.Optional;

public record UserInfoResponseDto (
        Integer userId,
        String email,
        String nickname,
        String profileImageUrl,
        String createdAt,
        String updatedAt
    ) {

    public static UserInfoResponseDto of(
            Integer userId,
            String email,
            String nickname,
            String profileImageUrl,
            String createdAt,
            String updatedAt
    ) {
        return new UserInfoResponseDto(
                userId, email, nickname, profileImageUrl, createdAt, updatedAt
        );
    }

    public static UserInfoResponseDto from(User user) {

        String profileUrl = user.getProfileImageUris();

        return of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                profileUrl,
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString()
        );
    }
}