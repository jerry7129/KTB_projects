package com.example.board_api.global.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilTest {

    @Test
    void extractsObjectKeyFromCloudFrontUrl() {
        assertThat(FileUtil.toFileKey(
                "https://d2rrn7nnlaxdl3.cloudfront.net/profile/1/image.png"
        )).isEqualTo("profile/1/image.png");
    }

    @Test
    void preservesObjectKeyWithoutTreatingPrefixAsHostname() {
        assertThat(FileUtil.toFileKey("profile/1/image.png"))
                .isEqualTo("profile/1/image.png");
    }

    @Test
    void supportsLegacyPublicPath() {
        assertThat(FileUtil.toFileKey("/public/temp/image.png"))
                .isEqualTo("temp/image.png");
    }
}
