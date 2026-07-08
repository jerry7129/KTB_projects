package com.example.board_api.user.controller.dto.response;

public record UserSignupResponseDto (
        String email,
        String createdAt
    ) {
    public static UserSignupResponseDto from(String email, String createdAt) {
        return new UserSignupResponseDto(
                email,
                createdAt
        );
    }
}
