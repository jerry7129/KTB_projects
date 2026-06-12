package com.example.board_api.user.repository;

import com.example.board_api.user.domain.UserRepositoryCustom;
import com.example.board_api.user.domain.entity.User;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.board_api.file.domain.entity.QPostImage.postImage;
import static com.example.board_api.file.domain.entity.QProfileImage.profileImage;
import static com.example.board_api.post.domain.entity.QPost.post;
import static com.example.board_api.post.domain.entity.QPostLike.postLike;
import static com.example.board_api.post.domain.entity.QPostStatus.postStatus;
import static com.example.board_api.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByEmailWithProfileImage(String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.profileImages).fetchJoin()
                        .where(user.email.eq(email))
                        .fetchOne()
        );
    }

    @Override
    public Optional<User> findByIdWithProfileImage(Integer userId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.profileImages).fetchJoin()
                        .where(user.id.eq(userId))
                        .fetchOne()
        );
    }

    // 특정 유저의 프로필 사진과 유저가 작성한 게시글에 있는 사진들의 키를 모두 조회하는 쿼리.
    @Override
    public List<String> findByUserIdWidthAllImageKeys(Integer userId) {

        // 프로필 이미지 키 조회.
        List<String> profileKeys = queryFactory
                .select(profileImage.fileKey)
                .from(profileImage)
                .where(profileImage.user.id.eq(userId))
                .fetch();

        // 유저가 작성한 게시글의 이미지 키 조회.
        List<String> postKeys = queryFactory
                .select(postImage.fileKey)
                .from(postImage)
                .join(postImage.post, post)
                .where(post.writer.id.eq(userId))
                .fetch();

        // 두 리스트 합쳐서 리턴.
        List<String> allKeys = new ArrayList<>();
        allKeys.addAll(profileKeys);
        allKeys.addAll(postKeys);
        return allKeys;
    }


    @Override
    public void deleteByIdWithProfileImageWithPost(Integer userId) {
        // 유저 프로필 사진 삭제
        queryFactory.delete(profileImage)
                .where(profileImage.user.id.eq(userId))
                .execute();

        // 유저가 좋아요 누른 내역 삭제
        queryFactory.delete(postLike)
                .where(postLike.user.id.eq(userId))
                .execute();

        // 유저가 작성한 게시글에 달린 좋아요 삭제
        queryFactory.delete(postLike)
                .where(postLike.post.id.in(
                        JPAExpressions.select(post.id)
                                .from(post)
                                .where(post.writer.id.eq(userId))
                ))
                .execute();

        // 유저가 작성한 게시글의 상태 삭제
        queryFactory.delete(postStatus)
                .where(postStatus.post.id.in(
                        JPAExpressions.select(post.id)
                                .from(post)
                                .where(post.writer.id.eq(userId))
                ))
                .execute();

        // 유저가 작성한 게시글의 이미지 삭제
        queryFactory.delete(postImage)
                .where(postImage.post.id.in(
                        // JPQL은 DELETE 문에서 직접 JOIN을 쓸 수 없어서 서브 쿼리 생성.
                        JPAExpressions.select(post.id)
                                .from(post)
                                .where(post.writer.id.eq(userId))
                ))
                .execute();

        // 유저가 작성한 게시글 삭제
        queryFactory.delete(post)
                .where(post.writer.id.eq(userId))
                .execute();

        // 유저 삭제
        queryFactory.delete(user)
                .where(user.id.eq(userId))
                .execute();
    }
}