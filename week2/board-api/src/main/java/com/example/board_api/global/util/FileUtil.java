package com.example.board_api.global.util;

import com.example.board_api.global.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class FileUtil {

    // 인스턴스화 방지
    private FileUtil() {
        throw new BusinessException("INTERNAL_SERVER_ERROR", "서버 내부의 오류가 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 전체 URL에서 도메인을 제외한 상대 경로(Path) 추출
     * 예: http://localhost:8080/public/img.jpg -> /public/img.jpg
     */
    public static String extractPathFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            URI uri = new URI(url);
            String path = uri.getPath();
            return path != null ? path : url;
        } catch (URISyntaxException e) {
            return url;
        }
    }

    /**
     * 상대 경로에 현재 서버 도메인을 붙여 전체 URL 생성
     * 예: /public/img.jpg -> http://localhost:8080/public/img.jpg
     */
    public static String toFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }
        // 이미 http로 시작하면 변환 없이 반환
        if (relativePath.startsWith("http")) {
            return relativePath;
        }

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();

        return baseUrl + relativePath;
    }

    // 바이트 단위를 읽기 쉬운 단위(KB, MB 등)로 변환
    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
        return String.format("%.1f %sB", (double)bytes / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}