package com.example.board_api.post.domain;

import com.example.board_api.post.domain.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostStatusRepository extends JpaRepository<PostStatus, Long>, PostStatusRepositoryCustom {
}
