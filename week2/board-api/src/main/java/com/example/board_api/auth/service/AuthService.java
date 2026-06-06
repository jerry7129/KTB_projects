package com.example.board_api.auth.service;

import com.example.board_api.auth.controller.dto.*;
import com.example.board_api.auth.domain.RefreshTokenRepository;
import com.example.board_api.auth.domain.entity.RefreshToken;
import com.example.board_api.auth.repository.JwtProvider;
import com.example.board_api.global.exception.AuthorizedException;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    @Transactional
    public LoginResultDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new AuthorizedException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new AuthorizedException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                new RefreshToken(
                        refreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new LoginResultDto(
                LoginResponseDto.of(user, accessToken, jwtProvider.getAccessTokenValidityInMilliseconds()),
                refreshToken
        );
    }

    // 액세스 토큰 재발급
    public TokenResultDto refreshAccessToken(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        if (saved.isExpired()) {
            refreshTokenRepository.delete(saved);
            throw new AuthorizedException("UNAUTHORIZED");
        }

        User user = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new AuthorizedException("UNAUTHORIZED"));

        String newAccessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );

        // Refresh token 회전 (Rotation)
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());
        refreshTokenRepository.delete(saved);
        refreshTokenRepository.save(
                new RefreshToken(
                        newRefreshToken,
                        user.getId(),
                        LocalDateTime.now().plusDays(14)
                )
        );

        return new TokenResultDto(
                new TokenInfoDto(newAccessToken, 3600L),
                newRefreshToken
        );
    }

    public void logout(String refreshToken) {
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }
    }
}
