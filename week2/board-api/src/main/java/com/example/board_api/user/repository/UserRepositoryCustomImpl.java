package com.example.board_api.user.repository;

import com.example.board_api.user.domain.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// UserRepositoryCustom interface의 메소드를 QueryDSL로 구현
@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
}
