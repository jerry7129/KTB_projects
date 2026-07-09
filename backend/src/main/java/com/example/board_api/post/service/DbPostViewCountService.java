package com.example.board_api.post.service;

import com.example.board_api.post.domain.PostStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DbPostViewCountService implements PostViewCountService {

    private final PostStatusRepository postStatusRepository;

    // 조회수 증가
    @Override
    @Transactional
    public void incrementViewCount(Long postId) {
        postStatusRepository.incrementViewCount(postId);
    }
}
