package com.example.board_api.global.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalImageService implements ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file) {
        // Image가 없다고 문제가 되지는 않음. 그냥 null을 리턴함.
        if(file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 파일 이름 생성 시, 중복 방지를 위해 UUID를 앞에 추가함.
            String fileName = UUID.randomUUID().toString().replace("-", "")
                    + "_" + file.getOriginalFilename();
            // 파일을 저장할 위치 생성.
            Path targetPath = Paths.get(uploadDir, fileName);
            // 생성할 디렉토리의 부모 디렉토리가 없을 경 자동 생성.
            Files.createDirectories(targetPath.getParent());

            File targetFile = targetPath.toFile();
            file.transferTo(targetFile);

            return "/images/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String imageUrl) {

    }

    @Override
    public String update(String oldImageUrl, MultipartFile newFile) {
        return "";
    }

}
