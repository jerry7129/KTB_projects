package com.example.board_api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String code;
    private final String message;
    private final HttpStatus status;
    
    public BusinessException(String code, String message, HttpStatus status) {
        super(code);
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
