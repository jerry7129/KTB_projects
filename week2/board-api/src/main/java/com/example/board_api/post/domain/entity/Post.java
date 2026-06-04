package com.example.board_api.post.domain.entity;

import com.example.board_api.file.domain.entity.File;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<File> image = new ArrayList<>();

    // Domain-Driven Design에서 서로 다른 Domain의 Entity를 직접 참조하는 것을 지양한다.
    // 그래서 게시글의 작성자인 User의 id를 authorId에 저장해둔다.
    // 이 id 값은 post 작성 시 http request message에 포함되어있다.
    // 이후 post와 user의 연결이 필요할 경우 authorId를 기준으로
    // repository에서 합쳐주면 된다.
//    private Long writerId;

    public Post(String title, String content, String image, Long writerId) {
        this.title = title;
        this.content = content;
//        this.image = image;
//        this.writerId = writerId;
    }

    public void changePost(String title, String content, String image){
        this.title = title;
        this.content = content;
//        this.image = image;
    }
}
