package com.example.board_api.post.repository;

import ch.qos.logback.core.util.StringUtil;
import com.example.board_api.post.domain.PostRepositoryCustom;
import com.example.board_api.post.domain.entity.Post;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.core.types.Expression;
import com.example.board_api.post.domain.entity.QPostStatus;
import java.util.ArrayList;
import java.util.List;

import static com.example.board_api.post.domain.entity.QPost.post;
import static com.example.board_api.post.domain.entity.QPostStatus.postStatus;
import static com.example.board_api.user.domain.entity.QUser.user;

@Repository("postRepositoryImpl")
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findPostsWithCursor(String keyword, String sort, String order, Long cursor, Pageable pageable) {
        return queryFactory.selectFrom(post)
                .leftJoin(post.writer, user).fetchJoin()
                .leftJoin(post.postStatus, postStatus).fetchJoin()
                .where(
                        cursorCondition(sort, order, cursor),
                        containKeyword(keyword)
                )
                .orderBy(getOrderSpecifiers(sort, order))
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private BooleanExpression cursorCondition(String sort, String order, Long cursor) {
        if (cursor == null) {
            return null;
        }

        // sort 값이 없으면 default는 시간순(postId)
        if (!StringUtils.hasText(sort) || "recent".equals(sort) || "id".equals(sort)) {
            return "asc".equalsIgnoreCase(order) ? post.id.gt(cursor) : post.id.lt(cursor);
        }

        // 조회수 기준 정렬
        if ("viewCount".equals(sort) || "view".equals(sort)) {
            QPostStatus subPostStatus = new QPostStatus("subPostStatus");
            // viewCount를 기준으로 커서 페이징
            Expression<Long> cursorViewCountQuery = JPAExpressions.select(subPostStatus.viewCount)
                    .from(subPostStatus)
                    .where(subPostStatus.id.eq(cursor));
            
            // order가 asc가 아니면 모두 desc
            if ("asc".equalsIgnoreCase(order)) {
                return postStatus.viewCount.gt(cursorViewCountQuery)
                        .or(postStatus.viewCount.eq(cursorViewCountQuery).and(post.id.gt(cursor)));
            } else {
                return postStatus.viewCount.lt(cursorViewCountQuery)
                        .or(postStatus.viewCount.eq(cursorViewCountQuery).and(post.id.lt(cursor)));
            }
        }

        // 좋아요수 기준 정렬
        if ("likeCount".equals(sort) || "like".equals(sort)) {
            QPostStatus subPostStatus = new QPostStatus("subPostStatus");
            Expression<Integer> cursorLikeCountQuery = JPAExpressions.select(subPostStatus.likeCount)
                    .from(subPostStatus)
                    .where(subPostStatus.id.eq(cursor));

            // order가 asc가 아니면 모두 desc
            if ("asc".equalsIgnoreCase(order)) {
                return postStatus.likeCount.gt(cursorLikeCountQuery)
                        .or(postStatus.likeCount.eq(cursorLikeCountQuery).and(post.id.gt(cursor)));
            } else {
                return postStatus.likeCount.lt(cursorLikeCountQuery)
                        .or(postStatus.likeCount.eq(cursorLikeCountQuery).and(post.id.lt(cursor)));
            }
        }
        
        return post.id.lt(cursor);
    }

    private BooleanExpression containKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return post.title.contains(keyword);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(String sort, String order) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        Order direction = "asc".equalsIgnoreCase(order) ? Order.ASC : Order.DESC;

        if (StringUtils.hasText(sort)) {
            switch (sort) {
                case "recent":
                case "id":
                    orders.add(new OrderSpecifier<>(direction, post.id));
                    break;
                case "viewCount":
                case "view":
                    orders.add(new OrderSpecifier<>(direction, postStatus.viewCount));
                    break;
                case "likeCount":
                case "like":
                    orders.add(new OrderSpecifier<>(direction, postStatus.likeCount));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(direction, post.id));
                    break;
            }
        } else {
            orders.add(new OrderSpecifier<>(direction, post.id));
        }

        // id를 2차 정렬 조건으로 추가하여 데이터 누락 방지
        if (StringUtils.hasText(sort) && !"recent".equals(sort) && !"id".equals(sort)) {
            orders.add(new OrderSpecifier<>(direction, post.id));
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
