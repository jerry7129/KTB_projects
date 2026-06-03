package com.example.board_api.file.domain;

import com.example.board_api.global.util.converter.EnumMapperType;
import com.example.board_api.global.util.converter.LegacyCodeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileCategory implements EnumMapperType {
    PROFILE_IMAGE(0 ),
    POST_IMAGE(1 );

    private final Integer legacyCode;

    @Override
    public Integer getLegacyCode() {
        return this.legacyCode;
    }

    @Converter(autoApply = true)
    public static class FileCategoryConverter extends LegacyCodeConverter<FileCategory> {

        public FileCategoryConverter() {
            super(FileCategory.class);
        }
    }
}