package com.example.board_api.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.example.board_api.post.domain.entity.QPostStatus.postStatus;

@Repository
@RequiredArgsConstructor
public class PostStatusQueryRepositoryImpl implements PostStatusQueryRepository {

    private final JPAQueryFactory queryFactory;

    // 조회수 증가
    @Override
    public void incrementViewCount(Long postId) {
        queryFactory.update(postStatus)
                .set(postStatus.viewCount, postStatus.viewCount.add(1L))
                .where(postStatus.id.eq(postId))
                .execute();
    }

    // 좋아요수 증가
    @Override
    public void incrementLikeCount(Long postId) {
        queryFactory.update(postStatus)
                .set(postStatus.likeCount, postStatus.likeCount.add(1))
                .where(postStatus.id.eq(postId))
                .execute();
    }

    // 좋아요수 감소
    @Override
    public void decrementLikeCount(Long postId) {
        queryFactory.update(postStatus)
                .set(postStatus.likeCount, postStatus.likeCount.subtract(1))
                .where(postStatus.id.eq(postId)
                        .and(postStatus.likeCount.gt(0))) // 음수 방지
                .execute();
    }

    // 댓글수 증가
    @Override
    public void incrementCommentCount(Long postId) {
        queryFactory.update(postStatus)
                .set(postStatus.commentCount, postStatus.commentCount.add(1L))
                .where(postStatus.id.eq(postId))
                .execute();
    }

    // 댓글수 감소
    @Override
    public void decrementCommentCount(Long postId) {
        queryFactory.update(postStatus)
                .set(postStatus.commentCount, postStatus.commentCount.subtract(1L))
                .where(postStatus.id.eq(postId)
                        .and(postStatus.commentCount.gt(0L))) // 음수 방지
                .execute();
    }
}
