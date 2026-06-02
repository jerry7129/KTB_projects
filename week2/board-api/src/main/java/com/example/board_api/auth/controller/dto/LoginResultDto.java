package com.example.board_api.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResultDto {

    // Controller에서 유저 정보 및 access token은 Response body에 보내고,
    //               refresh token은 Response header 쿠키에 보내야 해서,
    // 미리 구역을 나눴다가 Controller에서 각 위치에 맞게 보내기만 하면 됨.
    private LoginResponseDto response;  // 응답 바디용
    private String refreshToken;        // 쿠키용
}
