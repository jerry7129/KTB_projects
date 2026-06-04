package com.example.board_api.user.repository;

import com.example.board_api.user.domain.UserQueryRepository;
import com.example.board_api.user.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.example.board_api.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByEmailWithProfileImage(String email) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.profileImage).fetchJoin()
                        .where(user.email.eq(email))
                        .fetchOne()
        );
    }

    @Override
    public Optional<User> findByIdWithProfileImage(Long id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .leftJoin(user.profileImage).fetchJoin()
                        .where(user.id.eq(id))
                        .fetchOne()
        );
    }
}