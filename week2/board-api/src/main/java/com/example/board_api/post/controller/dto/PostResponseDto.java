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
    Integer likeCount,
    Long commentCount,
    Long viewCount,
    String createdAt,
    String updatedAt
    ) {

    public static PostResponseDto of (
        Long postId,
        String postTitle,
        String postContent,
        String postImageUrl,
        PostWriterResponseDto postWriter,
        Integer likeCount,
        Long commentCount,
        Long viewCount,
        String createdAt,
        String updatedAt
    ) {
        return new PostResponseDto(
                postId, postTitle, postContent,
                postImageUrl, postWriter,
                likeCount, commentCount, viewCount,
                createdAt, updatedAt
        );
    }

    public static PostResponseDto from(Post post) {
        String fullPostImageUrl = null;
        if (post.getPostImageUris() != null && !post.getPostImageUris().isEmpty()) {
            fullPostImageUrl = FileUtil.toFullUrl(post.getPostImageUris().get(0));
        }

        return of (
                post.getId(),
                post.getTitle(),
                post.getContent(),
                fullPostImageUrl,
                PostWriterResponseDto.from(post.getWriter()),
                post.getPostStatus().getLikeCount(),
                post.getPostStatus().getCommentCount(),
                post.getPostStatus().getViewCount(),
                post.getCreatedAt() != null ? post.getCreatedAt().toString() : null,
                post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null
        );
    }
}
