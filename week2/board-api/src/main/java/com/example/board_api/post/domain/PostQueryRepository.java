package com.example.board_api.post.domain;

import com.example.board_api.post.domain.entity.Post;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostQueryRepository {
    List<Post> findPostsWithCursor(Long cursor, Pageable pageable);
}
