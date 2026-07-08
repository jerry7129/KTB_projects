package com.example.board_api.post.domain.entity;

import com.example.board_api.file.domain.entity.PostImage;
import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = PROTECTED)
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private PostStatus postStatus;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> postComments = new ArrayList<>();


    @Builder
    public Post(String title, String content, User writer, List<PostImage> images) {
        this.title = title;
        this.content = content;
        this.setWriter(writer);
        if (images != null) {
            for (PostImage image : images) {
                postImages.add(image);
            }
        }
    }

    public void changePostInformation(String title, String content, PostImage postImage){
        this.title = title;
        this.content = content;
        this.changePostImage(postImage);
    }

    public List<String> getPostImageUris() {
        List<String> postImageUris = new ArrayList<>();
        for(PostImage postImage : postImages) {
            postImageUris.add("/public/" + postImage.getFileKey());
        }
        return postImageUris;
    }

    // ========== 연관 관계 편의 메소드 ============

    // 주인 Entity인 PostImage 에서 정의하는 것보다는 (이미지가 어떤 게시글에 속할 지 결정)
    // 게시글에 이미지를 추가하는 것이기 때문에 Post Entity에 정의했다.
    public void addPostImage(PostImage postImage) {
        postImage.setPost(this);
        this.postImages.add(postImage);
    }

    // 프로필 사진 변경
    public void changePostImage(PostImage postImage) {
        this.postImages.clear(); // 기존 이미지 참조 해제 (orphanRemoval에 의해 DB에서 삭제됨)
        if (postImage != null) {
            postImage.setPost(this);
            this.postImages.add(postImage);
        }
    }

    // 주인 Entity인 Post 에서 정의.
    // 게시글을 작성자가 쓴 게시글 리스트에 포함시킴.
    public void setWriter(User writer) {
        this.writer = writer;
        writer.addPost(this);
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
}
