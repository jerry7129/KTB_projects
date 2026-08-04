package com.example.board_api.post.domain;

import com.example.board_api.post.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndIdGreaterThanOrderByIdAsc(Long postId, Long id, Pageable pageable);
    List<Comment> findByPostIdOrderByIdAsc(Long postId, Pageable pageable);
}
