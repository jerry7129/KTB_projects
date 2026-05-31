package com.example.board_api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> of(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
