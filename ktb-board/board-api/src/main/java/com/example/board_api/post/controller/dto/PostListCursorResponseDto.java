package com.example.board_api.post.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostListCursorResponseDto {
    private List<PostResponseDto> posts;
    private Long startingAfter;
    private boolean hasNext;

    @Builder
    public PostListCursorResponseDto(List<PostResponseDto> posts, Long nextCursor, boolean hasNext) {
        this.posts = posts;
        this.startingAfter = nextCursor;
        this.hasNext = hasNext;
    }
}
