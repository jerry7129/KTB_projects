package com.example.board_api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE(0,"ACTIVE"),
    DELETED(1, "DELETED");

    private final int code;
    private final String description;

    // DB에서 꺼낸 tinyint를 다시 자바의 Enum 객체로 변환
    public static UserStatus fromCode(int dbCode) {
        return Arrays.stream(UserStatus.values())
                .filter(role -> role.getCode() == dbCode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 Role 입니다: " + dbCode));
    }
}
