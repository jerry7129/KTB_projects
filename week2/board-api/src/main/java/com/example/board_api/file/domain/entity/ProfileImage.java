package com.example.board_api.file.domain.entity;

import com.example.board_api.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "profile_images")
@NoArgsConstructor(access = PROTECTED)
public class ProfileImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    // Foreign Key를 갖고있는 곳에서 선언해야 함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false, name = "image_key")
    private String fileKey = "profile/default-profile.png";

    // Constructor
    public ProfileImage(String fileKey, Long userId) {
        this.fileKey = fileKey;
        this.userId = userId;
    }

    // Factory Methods
    public static ProfileImage createProfileImage(String fileKey, Long userId) {
        return new ProfileImage(fileKey, userId);
    }

    // 회원가입 시, 이미지 저장을 temp 디렉토리에 임시로 저장한 뒤에 회원가입 완료 후 경로를 바꾸기 위함.
    public void updateFileKeyAndUserId(String fileKey, Long userId) {
        this.fileKey = fileKey;
        this.userId = userId;
    }
}