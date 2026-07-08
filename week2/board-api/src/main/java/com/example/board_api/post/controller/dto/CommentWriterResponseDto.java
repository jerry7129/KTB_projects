package com.example.board_api.post.controller.dto;

import com.example.board_api.user.domain.entity.User;

public record CommentWriterResponseDto (
        Integer commentWriterId,
        String commentWriterNickname,
        String commentWriterProfileImageUrl
){

    public static CommentWriterResponseDto of (
            Integer commentWriterId,
            String commentWriterNickname,
            String commentWriterProfileImageUrl
    ) {
        return new CommentWriterResponseDto(commentWriterId, commentWriterNickname, commentWriterProfileImageUrl);
    }

    public static CommentWriterResponseDto from (User writer) {
        String profileUrl = writer.getProfileImageUris();

        return of (
                writer.getId(),
                writer.getNickname(),
                profileUrl
        );
    }
}
