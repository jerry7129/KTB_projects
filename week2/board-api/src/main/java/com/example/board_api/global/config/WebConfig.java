package com.example.board_api.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 현재 실행 중인 프로젝트의 절대 경로 찾기 (C:\Users\...\ProjectName)
        String rootPath = Paths.get(System.getProperty("user.dir"))
                .toAbsolutePath()
                .toString();

        // OS에 상관없이 파일 시스템을 가리키도록 접두어 붙이기
        // 윈도우/맥/리눅스 모두 호환되도록 "file:///" + 절대경로 + "/uploads/" 형태로 만듭니다.
        String uploadPath = "file:///" + rootPath + "/uploads/";

        // 설정 적용
        registry.addResourceHandler("/public/**")
                .addResourceLocations(uploadPath);
    }
}