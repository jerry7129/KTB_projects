package com.example.board_api.global.upload;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    // 이미지를 multi-part로 받아서 저장소에 저장 후 저장한 URL 주소를 반환
    String upload(MultipartFile file);

    // 저장소의 URL 주소에 있는 이미지를 제거
    void delete(String imageUrl);

    // 이미지를 변경할 때, 이전 URL 주소로 가서 기존 이미지를 삭제를 한 뒤, 새로운 파일을 저장 후 URL 반환
    String update(String oldImageUrl, MultipartFile newFile);
}
