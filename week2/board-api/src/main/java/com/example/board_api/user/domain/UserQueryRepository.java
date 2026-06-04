package com.example.board_api.user.domain;

import com.example.board_api.user.domain.entity.User;

import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findByEmailWithProfileImage(String email);
    Optional<User> findByIdWithProfileImage(Long id);
}