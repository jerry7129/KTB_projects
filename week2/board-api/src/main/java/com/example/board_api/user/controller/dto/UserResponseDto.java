package com.example.board_api.user.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import com.example.board_api.user.domain.entity.User;

@Getter
@JsonPropertyOrder({"userId", "email", "nickname", "profileImageURL", "createdAt", "updatedAt"})
public class UserResponseDto {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final String createdAt;
    private final String updatedAt;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.createdAt = user.getCreatedAt().toString();
        this.updatedAt = user.getUpdatedAt().toString();
    }
}
