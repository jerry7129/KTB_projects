package com.example.board_api.user.domain;

import com.example.board_api.global.util.converter.EnumMapperType;
import com.example.board_api.global.util.converter.LegacyCodeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole implements EnumMapperType {
    USER(0 ),
    ADMIN(1 );

    private final int legacyCode;

    @Override
    public Integer getLegacyCode() {
        return this.legacyCode;
    }

    @Converter(autoApply = true)
    public static class UserRoleConverter extends LegacyCodeConverter<UserRole> {

        public UserRoleConverter() {
            super(UserRole.class);
        }
    }
}