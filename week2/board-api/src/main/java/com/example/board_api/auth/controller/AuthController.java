package com.example.board_api.auth.controller;

import com.example.board_api.auth.controller.dto.*;
import com.example.board_api.auth.service.AuthService;
import com.example.board_api.global.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.refresh-token-exp-seconds}")
    private Long refreshTokenExpSeconds;

    // 로그인
    @PostMapping("/auth")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse httpResponse
    ) {
        // 로그인 처리
        LoginResultDto result = authService.login(loginRequestDto);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpSeconds)
                .sameSite("Strict")
                .build();

        // 쿠키를 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS", "로그인을 성공했습니다.", result.getResponse()));
    }

    // 액세스 토큰 재발급
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenInfoDto>> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenResultDto result = authService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(refreshTokenExpSeconds)
                    .sameSite("Lax")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("TOKEN_REFRESH_SUCCESS", "토큰 갱신을 성공했습니다.", result.getToken()));
    }

    // 로그아웃
    @DeleteMapping("/auth")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                                    HttpServletResponse httpResponse
    ) {
        // DB에서 리프레시 토큰 삭제
        authService.logout(refreshToken);
        // 브라우저 쿠키 삭제를 위해 maxAge(0)을 가진 빈 쿠키 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // HTTPS 운영 환경에서는 true 권장
                .path("/")
                .maxAge(0)    // 0초로 설정하여 브라우저에서 즉시 삭제 유도
                .sameSite("Strict")
                .build();

        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGOUT_SUCCESS", "로그아웃에 성공했습니다.", null));
    }
}