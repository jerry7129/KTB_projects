package com.example.board_api.file.controller.dto;

import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadResponseDto {

    private String fileUrl;

    public static ImageUploadResponseDto of(String fileUrl) {
        return new ImageUploadResponseDto(fileUrl);
    }

    public static ImageUploadResponseDto from(String fileKey) {
        // DB의 상대 경로(/public/...) 반환
        String url = "/public/" + fileKey;
        return of(url);
    }
}