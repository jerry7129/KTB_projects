package com.example.board_api.post.service;

// 조회수는 업데이트가 자주 일어나기 때문에, 별도의 처리를 해주면 좋다.
// 지금은 DB에서 업데이트를 하지만, 나중에 Redis까지 도입을 했을 때를 고려해서
// 조회수 증가 로직은 따로 분리를 해두었다.
public interface PostViewCountService {
    // 조회수 증가
    void incrementViewCount(Long postId);
}
