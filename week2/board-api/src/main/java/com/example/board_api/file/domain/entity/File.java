package com.example.board_api.file.domain.entity;

import com.example.board_api.file.domain.FileCategory;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "images")
@NoArgsConstructor(access = PROTECTED)
public class File {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "post_id")
    private Long postId;

    // Foreign Key를 갖고있는 곳에서 선언해야 함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Column(nullable = false, columnDefinition = "TINYINT", name = "image_category")
    private FileCategory fileCategory;

    @Column(nullable = false, name = "image_key")
    private String fileKey = "profile/default-profile.png";

    // Constructor
    public File(String fileKey, Long id, FileCategory fileCategory) {
        this.fileKey = fileKey;
        if(fileCategory == FileCategory.PROFILE_IMAGE) {
            this.userId = id;
        } else if(fileCategory == FileCategory.POST_IMAGE) {
            this.postId = id;
        } else {
            throw new IllegalArgumentException("유효하지 않은 파일입니다.");
        }

        this.fileCategory = fileCategory;
    }

    // Factory Methods
    public static File createProfileImage(String fileKey, Long userId) {
        return new File(fileKey, userId, FileCategory.PROFILE_IMAGE);
    }

    // 회원가입 시, 이미지 저장을 temp 디렉토리에 임시로 저장한 뒤에 회원가입 완료 후 경로를 바꾸기 위함.
    public void updateFileKeyAndUserId(String fileKey, Long userId) {
        this.fileKey = fileKey;
        this.userId = userId;
    }
}