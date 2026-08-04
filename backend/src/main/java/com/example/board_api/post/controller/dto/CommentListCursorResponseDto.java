package com.example.board_api.post.controller.dto;

import lombok.Builder;

import lombok.Getter;
import java.util.List;

@Getter
public class CommentListCursorResponseDto {
    private List<CommentResponseDto> comments;
    private Long startingAfter;
    private boolean hasNext;

    @Builder
    public CommentListCursorResponseDto(List<CommentResponseDto> comments, Long startingAfter, boolean hasNext) {
        this.comments = comments;
        this.startingAfter = startingAfter;
        this.hasNext = hasNext;
    }
}
