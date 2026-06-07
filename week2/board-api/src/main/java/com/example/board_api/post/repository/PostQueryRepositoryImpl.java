package com.example.board_api.post.repository;

import com.example.board_api.post.domain.PostQueryRepository;
import com.example.board_api.post.domain.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.board_api.post.domain.entity.QPost.post;
import static com.example.board_api.post.domain.entity.QPostStatus.postStatus;
import static com.example.board_api.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostsWithCursor(Long cursor, Pageable pageable) {
        return queryFactory.selectFrom(post)
                .leftJoin(post.writer, user).fetchJoin()
                .leftJoin(post.postStatus, postStatus).fetchJoin()
                .where(postIdLt(cursor))
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression postIdLt(Long cursor) {
        // 첫 페이지 조회 시, cursor 값이 null 임.
        if (cursor == null) {
            return null;
        }
        // cursor 보다 id 값 작은 애들을 가져옴
        // id 값이 작다 -> 더 오래된 게시글이다.
        return post.id.lt(cursor);
    }
}
