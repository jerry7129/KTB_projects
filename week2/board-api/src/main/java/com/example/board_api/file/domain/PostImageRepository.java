package com.example.board_api.file.domain;

import com.example.board_api.file.domain.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByFileKey(String fileKey);
}