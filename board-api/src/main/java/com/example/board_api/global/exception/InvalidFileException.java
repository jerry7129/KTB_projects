package com.example.board_api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InvalidFileException extends BusinessException {

  private final Map<String, String> errors;

  public InvalidFileException(String fieldName, String errorMessage) {
    // 부모 생성자 호출: Code는 "INVALID_INPUT", Status는 422로 고정
    super("INVALID_INPUT", "유효하지 않은 파일입니다.", HttpStatus.UNPROCESSABLE_CONTENT);

    this.errors = new HashMap<>();
    this.errors.put(fieldName, errorMessage);
  }
}