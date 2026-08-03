package com.example.board_api.post.domain;

import com.example.board_api.post.domain.entity.Post;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepositoryCustom {
    List<Post> findPostsWithCursor(String keyword, String sort, String order, Long cursor, Pageable pageable);
}
