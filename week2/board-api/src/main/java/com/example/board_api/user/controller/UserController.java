package com.example.board_api.user.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Transactional
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestPart(value = "data") UserRequestDto requestDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
            ) {
        UserResponseDto responseDto = userService.signup(requestDto, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("SUCCESS", "회원가입이 완료되었습니다.", responseDto));
    }
}
