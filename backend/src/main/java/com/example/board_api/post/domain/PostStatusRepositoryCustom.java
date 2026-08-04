package com.example.board_api.post.domain;

public interface PostStatusRepositoryCustom {
    void incrementViewCount(Long postId);
    void incrementLikeCount(Long postId);
    void decrementLikeCount(Long postId);
    void incrementCommentCount(Long postId);
    void decrementCommentCount(Long postId);
    Integer getLikeCount(Long postId);
    Long getCommentCount(Long postId);
}
