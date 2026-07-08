package com.example.board_api.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResultDto {

    private TokenInfoDto token;        // 응답 바디 (accessToken, expiresIn)
    private String newRefreshToken;    // 회전 시에만 사용 (없으면 null)
}
