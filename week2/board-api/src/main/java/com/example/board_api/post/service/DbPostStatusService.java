package com.example.board_api.post.service;

import com.example.board_api.post.repository.PostStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DbPostStatusService implements PostStatusService {

    private final PostStatusRepository postStatusRepository;

    // 조회수 증가
    @Override
    @Transactional
    public void incrementViewCount(Long postId) {
        postStatusRepository.incrementViewCount(postId);
    }

    // 좋아요수 증가
    @Override
    @Transactional
    public void incrementLikeCount(Long postId) {
        postStatusRepository.incrementLikeCount(postId);
    }

    // 좋아요수 감소
    @Override
    @Transactional
    public void decrementLikeCount(Long postId) {
        postStatusRepository.decrementLikeCount(postId);
    }

    // 댓글수 증가
    @Override
    @Transactional
    public void incrementCommentCount(Long postId) {
        postStatusRepository.incrementCommentCount(postId);
    }

    // 댓글수 감소
    @Override
    @Transactional
    public void decrementCommentCount(Long postId) {
        postStatusRepository.decrementCommentCount(postId);
    }
}
