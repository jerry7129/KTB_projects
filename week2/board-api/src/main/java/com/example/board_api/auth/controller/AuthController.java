package com.example.board_api.auth.controller;

import com.example.board_api.auth.controller.dto.LoginRequestDto;
import com.example.board_api.auth.controller.dto.LoginResponseDto;
import com.example.board_api.auth.controller.dto.LoginResultDto;
import com.example.board_api.auth.domain.RefreshTokenRepository;
import com.example.board_api.auth.repository.JwtProvider;
import com.example.board_api.auth.service.AuthService;
import com.example.board_api.global.ApiResponse;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.repository.JdbcUserRepository;
import com.example.board_api.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping
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
}
