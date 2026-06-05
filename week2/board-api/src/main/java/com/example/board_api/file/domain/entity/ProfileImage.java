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

    // Foreign Key를 갖고있는 곳에서 선언해야 함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "image_key")
    private String fileKey = "profile/default-profile.png";

    public ProfileImage(String fileKey) {
        this.fileKey = fileKey;
    }

    // 회원가입 시, 이미지 저장을 temp 디렉토리에 임시로 저장한 뒤에 회원가입 완료 후 경로를 바꾸기 위함.
    public void updateFileKeyAndUserId(String fileKey) {
        this.fileKey = fileKey;
    }

    // 연관 관계 편의 메소드를 위한 메소드
    public void setUser(User user) {
        this.user = user;
    }
}