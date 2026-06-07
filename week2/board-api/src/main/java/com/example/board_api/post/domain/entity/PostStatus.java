package com.example.board_api.post.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@lombok.Getter
public class PostStatus {

    @Id
    @Column(name = "post_id")
    private Long id;

    @Builder.Default()
    private Integer likeCount = 0;
    @Builder.Default()
    private Long commentCount = 0L;
    @Builder.Default()
    private Long viewCount = 0L;

    @LastModifiedDate
    private Instant updatedAt;

    @OneToOne
    @MapsId // Post의 Primary Key를 PostStatus에서도 Primary Key로 쓸 수 있게 해줌.
    @JoinColumn(name = "post_id")
    private Post post;

    // ======= 연관 관계 편의 메소드 =======
    // 현재 게시글 status를 게시글에 설정

    public void setPost(Post post) {
        this.post = post;
        post.setPostStatus(this);
    }
}
