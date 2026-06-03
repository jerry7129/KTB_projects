//package com.example.board_api.user.repository;
//
//import com.example.board_api.user.domain.UserQueryRepository;
//import com.example.board_api.user.domain.entity.User;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//import static com.example.board_api.user.domain.entity.QUser.user;
//
//@Repository
//@RequiredArgsConstructor
//public class UserQueryRepositoryImpl implements UserQueryRepository {
//    // 💡 EntityManager 대신 Spring Bean으로 등록된 JPAQueryFactory를 주입받습니다.
//    private final JPAQueryFactory queryFactory;
//
//    @Override
//    public Optional<User> findByEmailWithProfileImage(String email) {
//        User result = queryFactory
//                .selectFrom(user)
//                .leftJoin(user.profileImageUrl).fetchJoin()
//                .where(user.email.eq(email))
//                .fetchOne();
//
//        return Optional.ofNullable(result);
//    }
//
//    @Override
//    public Optional<User> findByIdWithProfileImage(Long id) {
//        User result = queryFactory
//                .selectFrom(user)
//                .leftJoin(user.profileImage).fetchJoin()
//                .where(user.id.eq(id))
//                .fetchOne();
//
//        return Optional.ofNullable(result);
//    }
//}
