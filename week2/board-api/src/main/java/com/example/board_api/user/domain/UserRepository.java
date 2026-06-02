package com.example.board_api.user.domain;

import com.example.board_api.user.domain.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 순수 JDBC로 구현을 하다가 JPA 적용 방식으로 수정함.
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    void deleteById(@NonNull Long userId);
}
