package com.example.board_api.post.repository;

import com.example.board_api.post.domain.entity.PostLike;
import com.example.board_api.post.domain.entity.PostLikeEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeEntityId> {
}
