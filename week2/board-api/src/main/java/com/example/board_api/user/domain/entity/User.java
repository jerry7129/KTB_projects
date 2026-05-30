package com.example.board_api.user.domain.entity;

import com.example.board_api.user.UserRole;
import com.example.board_api.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class User {

    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImageURL;
    private UserRole role;
    private UserStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    // User 정보 (닉네임, 프로필 사진 URL) 변경
    public void changeUserInformation(String nickname, String profileImageURL){
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 공백일 수 없습니다.");
        }
        this.nickname = nickname;
        this.profileImageURL = profileImageURL;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }
}
