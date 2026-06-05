package com.example.board_api.user.domain.entity;

import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ProfileImage> profileImage = new ArrayList<>();

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
    public void changeUserInformation(String nickname, ProfileImage profileImage){
        changeUserNickname(nickname);
        changeProfileImage(profileImage);
    }

    // 닉네임 변경
    public void changeUserNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 공백일 수 없습니다.");
        }
        this.nickname = nickname;
    }

    // 프로필 사진 변경
    public void changeProfileImage(ProfileImage profileImage) {
        this.profileImage.add(profileImage);
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    public String getProfileImageUrl() {
        if (this.profileImage == null) {
            return "/public/profile/default-profile.png";
        }
        return "/public/" + this.getProfileImage().getLast().getFileKey();
    }
}
