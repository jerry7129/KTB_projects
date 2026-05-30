package com.example.board_api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER(0,"USER"),
    ADMIN(1, "ADMIN");

    private final int code;
    private final String description;

}