package com.example.board_api.post.domain.entity;

import com.example.board_api.file.domain.entity.PostImage;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

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
    private Long writerId;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostImage> postImages = new ArrayList<>();

    // Domain-Driven Design에서 서로 다른 Domain의 Entity를 직접 참조하는 것을 지양한다.
    // 그래서 게시글의 작성자인 User의 id를 writerId에 저장해둔다.
    // 이 id 값은 post 작성 시 http request message에 포함되어있다.
    // 이후 post와 user의 연결이 필요할 경우 authorId를 기준으로
    // repository에서 합쳐주면 된다.

    @Builder
    public Post(String title, String content, Long writerId, List<PostImage> images) {
        this.title = title;
        this.content = content;
        this.writerId = writerId;
        if (postImages != null) {
            for (PostImage image : images) {
                postImages.add(image);
            }
        }
    }

    public void changePost(String title, String content){
        this.title = title;
        this.content = content;
    }

    // 연관 관계 편의 메소드
    // 주인 Entity인 PostImage 에서 정의하는 것보다는 (이미지가 어떤 게시글에 속할 지 결정)
    // 게시글에 이미지를 추가하는 것이기 때문에 Post Entity에 정의했다.
    public void addPostImage(PostImage postImage) {
        postImage.setPost(this);
        this.postImages.add(postImage);
    }
}
