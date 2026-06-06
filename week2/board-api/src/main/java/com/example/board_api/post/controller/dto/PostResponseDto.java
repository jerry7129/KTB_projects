package com.example.board_api.post.controller.dto;

import com.example.board_api.global.util.FileUtil;
import com.example.board_api.post.domain.entity.PostStatus;
import com.example.board_api.post.domain.entity.Post;

public record PostResponseDto (
    Long postId,
    String postTitle,
    String postContent,
    String postImageUrl,
    PostWriterResponseDto postWriter,
    String createdAt,
    String updatedAt
    ) {

    public static PostResponseDto of (
        Long postId,
        String postTitle,
        String postContent,
        String postImageUrl,
        PostWriterResponseDto postWriter,
        String createdAt,
        String updatedAt
    ) {
        return new PostResponseDto(
                postId, postTitle, postContent,
                postImageUrl, postWriter, createdAt, updatedAt
        );
    }

    public static PostResponseDto from(Post post, PostStatus postStatus) {
        String fullPostImageUrl = null;
        if (post.getPostImageUris() != null && !post.getPostImageUris().isEmpty()) {
            fullPostImageUrl = FileUtil.toFullUrl(post.getPostImageUris().get(0));
        }

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                fullPostImageUrl,
                PostWriterResponseDto.from(post.getWriter()),
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null,
                post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null
        );
    }
}
