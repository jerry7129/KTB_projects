package com.example.board_api.global.exception;

import org.springframework.http.HttpStatus;

public class AuthorizedException extends BusinessException {
    public AuthorizedException(String code) {
        super(code, "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED);
    }
}
