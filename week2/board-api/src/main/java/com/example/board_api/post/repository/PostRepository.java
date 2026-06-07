package com.example.board_api.post.repository;

import com.example.board_api.post.domain.entity.Post;
import com.example.board_api.post.repository.PostQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostQueryRepository {
}
