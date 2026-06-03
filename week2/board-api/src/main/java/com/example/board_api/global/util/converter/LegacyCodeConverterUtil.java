package com.example.board_api.global.util.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;

// 여러 Enum type에 적용할 수 있도록 Generic type으로 구현.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LegacyCodeConverterUtil {

    // legacyCode를 바탕으로 그에 맞는 enum type 반환
    // <T extends Enum<T> & EnumMapperType> -> T는 Enum type 이어야 하고, EnumMapperType을 구현해야 함.
    public static <T extends Enum<T> & EnumMapperType> T ofLegacyCode(Class<T> enumClass, Integer legacyCode) {
        if (legacyCode == null) {
            return null;
        }
        return EnumSet.allOf(enumClass).stream()
                .filter(v -> v.getLegacyCode().equals(legacyCode))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("enum=[%s], legacyCode=[%s]가 존재하지 않습니다.", enumClass.getName(), legacyCode)));
    }

    // enum type을 바탕으로 legacyCode 반환
    public static <T extends Enum<T> & EnumMapperType> Integer toLegacyCode(T enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.getLegacyCode();
    }
}