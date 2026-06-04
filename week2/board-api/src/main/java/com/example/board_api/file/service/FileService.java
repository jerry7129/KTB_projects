package com.example.board_api.file.service;

import com.example.board_api.file.domain.FileRepository;
import com.example.board_api.file.domain.entity.File;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.InvalidFileException;
import com.example.board_api.global.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.board_api.file.domain.entity.File.createProfileImage;

@Slf4j
@Service
public class FileService {

    // 로컬 저장소에 저장을 함.
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));  // 실제 저장 경로 (쓰기 가능)
    private static final Path FILE_DIR = PROJECT_ROOT.resolve("uploads");  // 프로필 이미지 저장 디렉토리
    private static final String FILE_URL = "/public";    // 클라이언트 접근 URL
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif"); // 허용할 확장자 목록
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File uploadProfileImage(MultipartFile file, Long userId) {
        return uploadFile(file, "profile", userId);
    }

    public File uploadTempProfileImage(MultipartFile file) {
        // 회원가입 전에는 userId가 없으므로 temp 디렉토리에 임시로 프로필 사진을 저장함.
        return uploadFile(file, "temp", null);
    }

    public File uploadFile(MultipartFile file, String prefix, Long id) {
        // prefix 맨 앞에 "/"를 붙여보내는 실수를 할 경우를 방지 하기 위한 방어 코드. (본인이 실수 함)
        if(prefix.startsWith("/")){
            prefix = prefix.substring(1);
        }

        String extension = extractAndValidateExtension(file); // 확장자 검증
        String filename = generateFilename(prefix, extension); // 파일명 생성: prefix-timestamp-uuid.extention

        // 실제 로컬 저장 경로: PROJECT_ROOT/uploads/profile/{userId}/{fileName}
        Path directoryPath = FILE_DIR.resolve(prefix);
        if (id != null) { // tmp에 임시 저장할 경우 id가 없어서 이에 대한 예외 처리.
            directoryPath = directoryPath.resolve(id.toString());
        }
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
        // 하지만, DB에는 /public/을 뺀 profile/{userId}/{filename} 만 저장한다.
        // AWS S3로 확장을 대비해 Object key로 저장하는 것.
        String fileKey = prefix + "/";
        if (id != null) { fileKey += id + "/"; } // 역시 id가 null 일 때 예외 처리.
        fileKey += filename; // 일반적인 경우 /profile/{userId}/{filename}, 임시의 경우 /tmp/{filename}
        return fileRepository.save(File.createProfileImage(fileKey, id));
    }

    public void delete(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) return;
        if (fileKey.equals("/images/default-profile.png")) return;

        try {
            // /public/profile/{userId}/{fileName} -> {userId}/{fileName} 상대 경로 변환 과정.
            // 실제 로컬 저장 경로는 PROJECT_ROOT/uploads/profile/{userId}/{fileName} 이다.
            String relativePath = fileKey.replace(FILE_URL + "/", "");
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
            log.warn("물리 파일 삭제 실패 (무시) 됨.: {}", fileKey, e);
        }

    }

    public File updateProfileImage(String oldImageUrl, MultipartFile newFile, Long userId) {
        return update(oldImageUrl, newFile, "profile", userId);
    }
    public File update(String oldImageUrl, MultipartFile newFile, String prefix, Long id) {
        delete(oldImageUrl);

        if (newFile == null || newFile.isEmpty()) {
            return null;
        }
        return uploadFile(newFile, prefix, id);
    }

    // 회원가입 전 임시로 저장한 파일의 디렉토리를 /profile/{userId}로 변경함.
    public File moveTempToProfile(String tempImageUrl, Long userId) {
        if (tempImageUrl == null || tempImageUrl.isBlank()) return null;

        // DB에서 임시 프로필 이미지 있는 지 찾기
        // URL로 왔을 경우, 상대 주소로 바꾸고 공통으로 붙는 /public/을 지워 fileKey 로 만든다.
        String extractedPath = FileUtil.extractPathFromUrl(tempImageUrl);
        String fileKey = extractedPath.replaceFirst("^/?public/", "");

        File file = fileRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "임시 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 로컬 저장소의 현재 임시 프로필 이미지 저장 경로 계산
        Path oldPath = FILE_DIR.resolve(fileKey).normalize();
        // 경로 변경할 위치 계산
        String newDirectory = "profile/" + userId;
        Path newDirectoryPath = FILE_DIR.resolve(newDirectory);
        Path newPath = newDirectoryPath.resolve(oldPath.getFileName()); // 파일명은 그대로 유지

        // 프로필 이미지 실제 저장 경로 변경 (temp -> profile/{userId})
        try {
            if (!Files.exists(newDirectoryPath)) {
                Files.createDirectories(newDirectoryPath);
            }
            // 파일 이동 및 목적지에 같은 이름이 있을 경우 덮어쓰기
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("파일 이동 실패: {} -> {}", oldPath, newPath, e);
            throw new BusinessException("INTERNAL_SERVER_ERROR", "파일 이동에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // DB 엔티티 정보 업데이트
        String imageKey = newDirectory + "/" + oldPath.getFileName().toString();
        file.updateFileKeyAndUserId(imageKey, userId);

        // JPA의 영속성 컨텍스트 변경 감지(Dirty Checking)로 자동 업데이트 됩니다.
        return file;
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
