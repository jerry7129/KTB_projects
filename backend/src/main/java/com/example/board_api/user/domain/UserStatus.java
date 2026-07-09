package com.example.board_api.user.domain;

import com.example.board_api.global.util.converter.EnumMapperType;
import com.example.board_api.global.util.converter.LegacyCodeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserStatus implements EnumMapperType {
    ACTIVE(0 ),
    DELETED(1 );

    private final int legacyCode;

    @Override
    public Integer getLegacyCode() {
        return this.legacyCode;
    }

    @Converter(autoApply = true)
    public static class UserStatusConverter extends LegacyCodeConverter<UserStatus> {

        public UserStatusConverter() {
            super(UserStatus.class);
        }
    }
}
