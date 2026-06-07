package com.example.board_api.post.repository;

import com.example.board_api.post.domain.entity.PostStatus;
import com.example.board_api.post.repository.PostStatusQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStatusRepository extends JpaRepository<PostStatus, Long>, PostStatusQueryRepository {
}
