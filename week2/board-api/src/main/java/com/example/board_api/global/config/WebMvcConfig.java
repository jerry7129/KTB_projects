package com.example.board_api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.yml에 적어둔 로컬 저장 폴더 경로
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 프론트엔드에서 /images/~~ 라고 요청이 오면
        registry.addResourceHandler("/images/**")
                // 내 컴퓨터의 uploadDir 폴더 안을 뒤져서 사진을 꺼내주라는 마법의 설정!
                .addResourceLocations("file:" + uploadDir);
    }
}