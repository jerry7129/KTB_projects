package com.example.board_api.post.service;

public interface PostStatusService {
    // 조회수 증가
    void incrementViewCount(Long postId);

    // 좋아요수 증가, 감소
    void incrementLikeCount(Long postId);
    void decrementLikeCount(Long postId);

    // 댓글 수 증가, 감소
    void incrementCommentCount(Long postId);
    void decrementCommentCount(Long postId);
}
