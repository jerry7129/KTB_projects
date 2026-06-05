package com.example.board_api.file.controller;

import com.example.board_api.file.controller.dto.ProfileImageUploadResponseDto;
import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.file.service.FileService;
import com.example.board_api.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 회원 가입 전 임시 프로필 이미지 업로드
    @PostMapping("/users/temp-profile-image")
    public ResponseEntity<ApiResponse<ProfileImageUploadResponseDto>> uploadTempProfileImage(
            @RequestPart("profileImage") MultipartFile file
    ) throws FileUploadException {
        ProfileImage savedFile = fileService.uploadTempProfileImage(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "PROFILE_IMAGE_UPLOADED",
                        "회원 가입을 위한 프로필 이미지가 업로드 되었습니다.",
                        ProfileImageUploadResponseDto.from(savedFile)
                ));
    }

    // 프로필 이미지 업로드
    @PostMapping("/users/me/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageUploadResponseDto>> uploadProfileImage(
            @AuthenticationPrincipal Long userId,
            @RequestPart("profileImage") MultipartFile file
    ) throws FileUploadException {
        ProfileImage savedFile = fileService.uploadProfileImage(file, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(
                        "PROFILE_IMAGE_UPLOADED",
                        "회원 가입을 위한 프로필 이미지가 업로드 되었습니다.",
                        ProfileImageUploadResponseDto.from(savedFile)
                ));
    }
}