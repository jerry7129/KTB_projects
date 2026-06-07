package com.example.board_api.post.domain;

import com.example.board_api.post.domain.entity.PostLike;
import com.example.board_api.post.domain.entity.PostLikeEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeEntityId> {
}
