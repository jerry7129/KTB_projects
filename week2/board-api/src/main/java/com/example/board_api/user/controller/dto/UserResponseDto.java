package com.example.board_api.user.controller.dto;

import lombok.Getter;
import com.example.board_api.user.domain.entity.User;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
