package com.example.board_api.post.controller.dto;

import com.example.board_api.global.util.FileUtil;
import com.example.board_api.user.domain.entity.User;

public record PostWriterResponseDto (
    Integer postWriterId,
    String postWriterNickname,
    String postWriterProfileImageUrl
    ){

    public static PostWriterResponseDto of (
        Integer postWriterId,
        String postWriterNickname,
        String postWriterProfileImageUrl
        ) {
        return new PostWriterResponseDto(postWriterId, postWriterNickname, postWriterProfileImageUrl);
    }

    public static PostWriterResponseDto from (User writer) {
        String fullProfileUrl = FileUtil.toFullUrl(writer.getProfileImageUris());

        return of (
                writer.getId(),
                writer.getNickname(),
                fullProfileUrl
        );
    }
}
