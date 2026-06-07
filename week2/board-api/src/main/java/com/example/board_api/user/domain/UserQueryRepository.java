package com.example.board_api.user.domain;

import com.example.board_api.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

// Spring Data JPA 로 해결하기 어려운 복잡한 쿼리는 이 인터페이스에서 정의함.
public interface UserQueryRepository {
    Optional<User> findByEmailWithProfileImage(String email);
    Optional<User> findByIdWithProfileImage(Long id);
    List<String> findByUserIdWidthAllImageKeys(Long UserId); // 사용자의 프로필 사진 및 게시글 사진 주소를 가져옴

    void deleteByIdWithProfileImageWithPost(Long id);
}