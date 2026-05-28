package com.example.board_api.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String nickname;
    private String profileImage;

    // User 생성자
    public User(String email, String password, String nickname, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // User 정보 (닉네임, 프로필 사진 URL) 변경
    public void changeUserInformation(String nickname, String profileImage){
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }
}
