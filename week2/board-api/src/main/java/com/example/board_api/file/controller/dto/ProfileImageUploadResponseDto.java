package com.example.board_api.file.controller.dto;

import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageUploadResponseDto {

    private String fileUrl;

    public static ProfileImageUploadResponseDto of(String fileUrl) {
        return new ProfileImageUploadResponseDto(fileUrl);
    }

    public static ProfileImageUploadResponseDto from(ProfileImage file) {
        // DB의 상대 경로(/public/...)를 전체 URL(http://...)로 변환
        String fullUrl = FileUtil.toFullUrl("/public/" + file.getFileKey());

        return of(fullUrl);
    }
}