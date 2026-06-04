package com.example.board_api.post.controller.dto;

import lombok.Getter;
import com.example.board_api.post.domain.entity.Post;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String postTitle;
    private String postContent;
    private String postImage;
    private Long postWriterId;

    public PostResponseDto(Post post) {
        postId = post.getId();
        postTitle = post.getTitle();
        postContent = post.getContent();
//        postImage = post.getImage();
    }
}
