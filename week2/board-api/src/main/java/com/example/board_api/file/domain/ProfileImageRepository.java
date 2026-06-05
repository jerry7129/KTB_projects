package com.example.board_api.file.domain;

import com.example.board_api.file.domain.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByFileKey(String fileKey);
}
