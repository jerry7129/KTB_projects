package com.example.board_api.user.domain.entity;

import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.post.domain.entity.Post;
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
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private UserRole role;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private UserStatus status;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;


    // CascadeType.ALL -> 부모 entity가 삭제될 경우 자식도 모두 삭제함.
    // orphanRemoval -> 자식 entity를 부모 List에서 제거할 경우 (고아 상태) DB에서도 삭제
    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileImage> profileImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> post = new ArrayList<>();

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

    // 비밀번호 변경
    public void changePassword(String password) {
        this.password = password;
    }

    // 프로필 이미지 Uri 가져오기
    public String getProfileImageUris() {
        if (this.profileImages == null) {
            return "/public/profile/default-profile.png";
        }
        return "/public/" + this.getProfileImages().getLast().getFileKey();
    }

    // ============ 연관 관계 편의 메소드 ===========
    // 프로필 사진 변경
    public void changeProfileImage(ProfileImage profileImage) {
        if (!this.profileImages.isEmpty() && profileImage != null) {
            if (this.profileImages.getLast().getFileKey().equals(profileImage.getFileKey())) {
                return; // 동일한 이미지면 변경 생략
            }
        }
        this.profileImages.clear(); // 기존 이미지 참조 해제 (orphanRemoval에 의해 DB에서 삭제됨)
        if (profileImage != null) {
            profileImage.setUser(this);
            this.profileImages.add(profileImage);
        }
    }

    // 프로필 사진 추가
    public void addProfileImage(ProfileImage profileImage) {
        profileImage.setUser(this);
        this.profileImages.add(profileImage);
    }

    // 게시글 추가
    public void addPost(Post post) {
        this.post.add(post);
    }
}
