package com.example.board_api.auth.controller;

import com.example.board_api.auth.controller.dto.LoginRequestDto;
import com.example.board_api.auth.controller.dto.LoginResponseDto;
import com.example.board_api.auth.controller.dto.LoginResultDto;
import com.example.board_api.auth.service.AuthService;
import com.example.board_api.global.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController와 @Controller의 차이
// Controller는 String 반환 시, 이를 클라이언트에게 보여줄 HTML 파일 이름으로 해석함.
// RestController는 순수하게 JSON만 반환함
// 즉, RestController를 사용할 경우, redirection 구현은 안 하는게 맞음.

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse httpResponse
    ){
        // 로그인 처리
        LoginResultDto result = authService.login(loginRequestDto);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        // 쿠키를 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS", "로그인을 성공했습니다.", result.getResponse()));
    }

    // 로그아웃
    @DeleteMapping("/auth")
    public void logout() {

    }
}
