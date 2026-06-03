package com.example.board_api.user.domain.entity;

import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Builder.Default
    private String profileImageUrl = "/public/default-profile.png";

    @Column(nullable = false, columnDefinition = "TINYINT")
    private UserRole role;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private UserStatus status;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // User 정보 (닉네임, 프로필 사진 URL) 변경
    public void changeUserInformation(String nickname, String profileImageUrl){
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 공백일 수 없습니다.");
        }
        this.nickname = nickname;
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    // 프로필 사진 URL 변경
    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }
}
