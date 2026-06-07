package com.example.board_api.post.repository;

public interface PostStatusQueryRepository {
    void incrementViewCount(Long postId);
    void incrementLikeCount(Long postId);
    void decrementLikeCount(Long postId);
    void incrementCommentCount(Long postId);
    void decrementCommentCount(Long postId);
}
