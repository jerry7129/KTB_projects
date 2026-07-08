package com.example.board_api.post.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String postTitle;
    private String postContent;
    private String postImageUrl;
}
