package com.example.board_api.user.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> signUp(
            @Valid @RequestPart(value = "data") UserRequestDto.SignUp requestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
            ) {
        UserResponseDto responseDto = userService.createUser(requestDto, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("SIGNUP_SUCCESS", "회원가입이 완료되었습니다.", responseDto));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserInfo(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestPart(value = "data") UserRequestDto.UpdateInfo requestDto,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
            ) {
        UserResponseDto responseDto = userService.updateUserInfo(userId, requestDto, profileImage);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_SUCCESS", "회원 정보 수정이 완료되었습니다.", responseDto));
    }
}
