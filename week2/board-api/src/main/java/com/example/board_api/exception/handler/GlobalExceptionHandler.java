package com.example.board_api.exception.handler;

import com.example.board_api.ApiResponse;
import com.example.board_api.exception.BusinessException;
import com.example.board_api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Server;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.of(exception.getCode(), exception.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.of(exception.getCode(), exception.getMessage(), null));
    }

    // User Controller의 UserRequestDto Valid 검사는 스프링이 만든 MethodArgumentNotValidException을 보낸다.
    // Custom Exception과 달리 code와 message를 분리할 수 없어서 defaultMessage에 code|message 형태로 저장한 뒤
    // Exception handler에서 둘을 나누어주었다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException exception) {

        // spring이 만든 exception의 내부 저장소를 열어서 실패한 field를 찾는다.
        FieldError fieldError = exception.getBindingResult().getFieldError();

        if (fieldError != null) {
            // 찾은 field의 message를 찾는다.
            String defaultMessage = fieldError.getDefaultMessage();

            // defaultMessage의 code와 message를 나눈다.
            if (defaultMessage != null && defaultMessage.contains("|")) {
                String[] parts = defaultMessage.split("\\|");
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.of(parts[0], parts[1], null));
            }
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of("VALIDATION_ERROR", defaultMessage, null));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of("UNKNOWN_ERROR", "잘못된 요청입니다.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleExceptions(Exception exception) {
        log.error("[Internal Server Error]: ", exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(
                        "INTERNAL_SERVER_ERROR",
                        "서버에서 알 수 없는 오류가 발생했습니다.", null));
    }
}
