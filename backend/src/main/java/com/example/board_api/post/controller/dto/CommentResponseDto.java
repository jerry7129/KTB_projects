package com.example.board_api.post.controller.dto;

public record CommentResponseDto (
        Long commentId,
        String commentContent,
        CommentWriterResponseDto commentWriter,
        Long commentCount,
        String createdAt,
        String updatedAt
) {
}
