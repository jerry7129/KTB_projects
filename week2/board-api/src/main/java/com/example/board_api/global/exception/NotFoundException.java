package com.example.board_api.global.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException {
    public NotFoundException(String code) {
        super(code, "유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
