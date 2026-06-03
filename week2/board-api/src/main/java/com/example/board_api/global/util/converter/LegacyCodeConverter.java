package com.example.board_api.global.util.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter(autoApply = true)
@RequiredArgsConstructor
// <E extends Enum<E> & EnumMapperType> -> E는 Enum type 이어야 하고, EnumMapperType을 구현해야 함.
public abstract class LegacyCodeConverter<E extends Enum<E> & EnumMapperType> implements AttributeConverter<E, Integer> {

    private final Class<E> targetEnumClass;

    @Override
    // java의 enum type을 DB에 저장할 때는 tinyint type인 legacyCode로 저장
    public Integer convertToDatabaseColumn(E attribute) {
        return LegacyCodeConverterUtil.toLegacyCode(attribute);
    }

    @Override
    // DB에 저장된 legacyCode를 바탕으로 java의 enum type으로 변환
    public E convertToEntityAttribute(Integer dbData) {
        return LegacyCodeConverterUtil.ofLegacyCode(targetEnumClass, dbData);
    }
}