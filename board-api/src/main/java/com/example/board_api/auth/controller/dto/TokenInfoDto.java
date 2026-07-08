package com.example.board_api.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfoDto {

    private String accessToken;
    private Long expiresIn;
}
