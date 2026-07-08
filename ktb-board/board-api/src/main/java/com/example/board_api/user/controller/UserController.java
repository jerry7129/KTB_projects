package com.example.board_api.user.controller;

import com.example.board_api.global.ApiResponse;
import com.example.board_api.user.controller.dto.request.UserRequestDto;
import com.example.board_api.user.controller.dto.response.UserInfoResponseDto;
import com.example.board_api.user.controller.dto.response.UserSignupResponseDto;
import com.example.board_api.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signUp(
            @Valid @RequestBody UserRequestDto.SignUp requestDto
            ) {
        UserSignupResponseDto responseDto = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("SIGNUP_SUCCESS", "회원가입이 완료되었습니다.", responseDto));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getUserInfo(
            @AuthenticationPrincipal Integer userId
    ) {
        UserInfoResponseDto responseDto = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("GET_SUCCESS", "회원 정보 조회가 완료되었습니다.", responseDto));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> updateUserInfo(
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody UserRequestDto.UpdateInfo requestDto
            ) {
        UserInfoResponseDto responseDto = userService.updateUserInfo(userId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("UPDATE_SUCCESS", "회원 정보 수정이 완료되었습니다.", responseDto));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUserInfo(
            @AuthenticationPrincipal Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("DELETE_SUCCESS", "회원 탈퇴가 완료되었습니다.", null));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody UserRequestDto.UpdatePassword requestDto
            ) {
        userService.updateUserPassword(userId, requestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("UPDATE_SUCCESS", "비밀번호 수정이 완료되었습니다.", null));
    }

    @GetMapping(params = "email")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@RequestParam("email") String email) {
        userService.checkEmail(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("CHECK_SUCCESS", "사용 가능한 이메일입니다.", null));
    }

    @GetMapping(params = "nickname")
    public ResponseEntity<ApiResponse<Void>> checkNickname(@RequestParam("nickname") String nickname) {
        userService.checkNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of("CHECK_SUCCESS", "사용 가능한 닉네임입니다.", null));
    }
}
