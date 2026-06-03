package com.example.board_api.global.util.file;

import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    // 로컬 저장소에 저장을 함.
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));  // 실제 저장 경로 (쓰기 가능)
    private static final Path FILE_DIR = PROJECT_ROOT.resolve("uploads");  // 프로필 이미지 저장 디렉토리
    private static final String FILE_URL = "/public";    // 클라이언트 접근 URL
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif"); // 허용할 확장자 목록

    public String uploadProfileImage(MultipartFile file, Long userId) {
        String directory = "profile/" + userId;
        return uploadFile(file, directory);
    }

    public String uploadFile(MultipartFile file, String directory) {
        // directory 맨 앞에 "/"를 붙여보내는 실수를 할 경우를 방지 하기 위한 방어 코드. (본인이 실수 함)
        if(directory.startsWith("/")){
            directory = directory.substring(1);
        }
        // profile/{userId} 형식으로 왔을 때, "/" 앞의 profile만 분리함.
        int prefixIndex = directory.indexOf("/");
        String filePrefix = (prefixIndex != -1) ? directory.substring(0, prefixIndex) : directory;

        String extension = extractAndValidateExtension(file); // 확장자 검증
        String filename = generateFilename(filePrefix, extension); // 파일명 생성: prefix-timestamp-uuid.extention

        // 실제 로컬 저장 경로: /uploads/profile/{userId}/{fileName}
        Path directoryPath = FILE_DIR.resolve(directory);
        Path savePath = directoryPath.resolve(filename);

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            file.transferTo(savePath.toFile());
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", savePath, e);
            throw new BusinessException("INTERNAL_SERVER_ERROR", "파일 저장을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 웹 URL 경로: /public/profile/{userId}/{filename}
        return FILE_URL + "/" + directory + "/" + filename;
    }

    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        if (imageUrl.equals("/images/default-profile.png")) return;

        try {
            // /public/profile/{userId}/{fileName} -> {userId}/{fileName} 상대 경로 변환 과정.
            // 실제 로컬 저장 경로는 PROJECT_ROOT/uploads/profile/{userId}/{fileName} 이다.
            String relativePath = imageUrl.replace(FILE_URL + "/", "");
            Path filePath = FILE_DIR.resolve(relativePath).normalize();
            // normalize()는 Path의 ../ 나 ./ 같이 위험한 경로를 없앤다.
            // 이로 인해 악의적 API 조작을 방지한다.

            // file 경로가 FILE_DIR 가 아닌 다른 경로일 경우 차단함.
            if (!filePath.startsWith(FILE_DIR)) {
                throw new BusinessException("FORBIDDEN", "잘못된 파일 삭제 요청입니다.", HttpStatus.FORBIDDEN);
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 파일 삭제를 실패했을 경우, Transaction을 Rollback 하면 안된다.
            // 그 이유는, 파일 삭제는 무조건 성공시킬 필요는 없다. 사진 업로드의 경우 반드시 업로드가 되어야,
            // 서비스가 정상적으로 돌아가는데, 삭제는 유저 입장에서는 크게 문제가 없다.
            // 삭제 실패한 파일은 나중에 서버가 자체적으로 Garbage Collection 등으로 지워주면 된다.
            log.warn("물리 파일 삭제 실패 (무시) 됨.: {}", imageUrl, e);
        }

    }

    public String updateProfileImage(String oldImageUrl, MultipartFile newFile, Long userId) {
        String directory = "profile/" + userId;
        return update(oldImageUrl, newFile, directory);
    }
    public String update(String oldImageUrl, MultipartFile newFile, String directory) {
        delete(oldImageUrl);

        if (newFile == null || newFile.isEmpty()) {
            return null;
        }
        return uploadFile(newFile, directory);
    }

    // ========== Private Methods ==========

    // 확장자 추출 및 검증
    private String extractAndValidateExtension(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new InvalidFileException(file.getName(), "FILE_NAME_REQUIRED");
        }

        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(file.getName(), "INVALID_FILE_EXTENSION"); // 허용되지 않은 확장자
        }

        return extension;
    }

    // 파일명 생성 통합 (prefix만 받아서 처리)
    private String generateFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String uuid = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        // 예: profile-20260113...jpg
        return prefix + "-" + timestamp + "-" + uuid + "." + extension;
    }
}
