package com.example.board_api.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER(0,"USER"),
    ADMIN(1, "ADMIN");

    private final int code;
    private final String description;

    // DB에서 꺼낸 tinyint를 다시 자바의 Enum 객체로 변환
    public static UserRole fromCode(int dbCode) {
        return Arrays.stream(UserRole.values())
                .filter(role -> role.getCode() == dbCode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 Role 입니다: " + dbCode));
    }
}