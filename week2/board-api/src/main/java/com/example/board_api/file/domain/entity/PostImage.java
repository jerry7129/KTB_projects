package com.example.board_api.file.domain.entity;

import com.example.board_api.file.domain.FileCategory;
import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "post_images")
@NoArgsConstructor(access = PROTECTED)
public class PostImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    // Foreign Key를 갖고있는 곳에서 선언해야 함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, name = "image_key")
    private String fileKey = "profile/default-profile.png";

    public PostImage(String fileKey) {
        this.fileKey = fileKey;
    }

    // 연관 관계 편의 메소드
    public void setPost(Post post) {
        this.post = post;
    }

}