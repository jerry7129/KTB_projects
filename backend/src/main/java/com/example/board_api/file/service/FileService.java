package com.example.board_api.file.service;

import com.example.board_api.file.repository.PostImageRepository;
import com.example.board_api.file.repository.ProfileImageRepository;
import com.example.board_api.file.domain.entity.PostImage;
import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.InvalidFileException;
import com.example.board_api.global.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
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
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
public class FileService {

    // 로컬 저장소에 저장을 함.
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));  // 실제 저장 경로 (쓰기 가능)
    private static final Path FILE_DIR = PROJECT_ROOT.resolve("uploads");  // 프로필 이미지 저장 디렉토리
    private static final String FILE_URL = "/public";    // 클라이언트 접근 URL
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif"); // 허용할 확장자 목록
    private static ProfileImageRepository profileImageRepository;
    private static PostImageRepository postImageRepository;
    private final S3Client s3Client;
    private final String storageType;
    private final String bucketName;

    public FileService(
            ProfileImageRepository profileImageRepository,
            PostImageRepository postImageRepository,
            ObjectProvider<S3Client> s3ClientProvider,
            @Value("${file.storage:local}") String storageType,
            @Value("${file.s3.bucket:}") String bucketName
    ) {
        FileService.profileImageRepository = profileImageRepository;
        FileService.postImageRepository = postImageRepository;
        this.s3Client = s3ClientProvider.getIfAvailable();
        this.storageType = storageType;
        this.bucketName = bucketName;
    }

    // ============= 이미지 업로드 (프로필, 게시글 사진) ===============
    public ProfileImage uploadProfileImage(MultipartFile file, Integer userId) {
        String fileKey = uploadFile(file, "profile", Long.valueOf(userId));
        ProfileImage image = new ProfileImage(fileKey);
        return profileImageRepository.save(image);
    }

    public PostImage uploadPostImage(MultipartFile file, Long postId) {
        String fileKey = uploadFile(file, "post", postId);
        PostImage image = new PostImage(fileKey);
        return postImageRepository.save(image);
    }

    // ============= 임시 이미지 업로드 (프로필, 게시글 사진) ===============
    public ProfileImage uploadTempProfileImage(MultipartFile file) {
        // 회원가입 전에는 userId가 없으므로 temp 디렉토리에 임시로 프로필 사진을 저장함.
        String fileKey = uploadFile(file, "profile", null);
        ProfileImage tempImage = new ProfileImage(fileKey);
        // 임시 파일의 경우 연관된 User가 없기 때문에, 수동으로 Repository에 접근함.
        return profileImageRepository.save(tempImage);
    }

    public PostImage uploadTempPostImage(MultipartFile file) {
        // 게시글 생성 전에는 postId가 없으므로 temp 디렉토리에 임시로 게시글 사진을 저장함.
        String fileKey = uploadFile(file, "post", null);
        PostImage tempImage = new PostImage(fileKey);
        // 임시 파일의 경우 연관된 Post가 없기 때문에, 수동으로 Repository에 접근함.
        return postImageRepository.save(tempImage);
    }

    public String uploadFile(MultipartFile file, String prefix, Long id) {
        // prefix 맨 앞에 "/"를 붙여보내는 실수를 할 경우를 방지 하기 위한 방어 코드. (본인이 실수 함)
        if(prefix.startsWith("/")){
            prefix = prefix.substring(1);
        }

        String extension = extractAndValidateExtension(file); // 확장자 검증
        String filename = generateFilename(prefix, extension); // 파일명 생성: prefix-timestamp-uuid.extention

        // id가 null일 때는 임시 저장이므로 temp 디렉토리를 사용
        String directoryName = (id == null) ? "temp" : prefix + "/" + id;
        String fileKey = directoryName + "/" + filename;

        if (isS3Storage()) {
            uploadToS3(file, fileKey);
            return fileKey;
        }

        Path directoryPath = FILE_DIR.resolve(directoryName);
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

        // 웹 URL 경로: /public/{directoryName}/{filename}
        // 하지만, DB에는 /public/을 뺀 경로만 저장한다.
        // AWS S3로 확장을 대비해 Object key로 저장하는 것.
        return fileKey;
    }

    // ============= 이미지 삭제 (프로필, 게시글 사진) ===============
    public void delete(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) return;
        if (fileKey.equals("profile/default-profile.png")) return;

        String normalizedKey = FileUtil.toFileKey(fileKey);
        if (isS3Storage()) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(normalizedKey)
                        .build());
            } catch (S3Exception e) {
                log.warn("S3 파일 삭제 실패 (무시): bucket={}, key={}", bucketName, normalizedKey, e);
            }
            return;
        }

        try {
            // /public/profile/{userId}/{fileName} -> {userId}/{fileName} 상대 경로 변환 과정.
            // 실제 로컬 저장 경로는 PROJECT_ROOT/uploads/profile/{userId}/{fileName} 이다.
            String relativePath = normalizedKey.replace(FILE_URL + "/", "");
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

    // ============= 이미지 업데이트 (프로필, 게시글 사진) ===============
    public ProfileImage updateProfileImage(String oldImageUrl, MultipartFile newFile, Integer userId) {
        String fileKey = update(oldImageUrl, newFile, "profile", Long.valueOf(userId));
        return new ProfileImage(fileKey);
    }

    public PostImage updatePostImage(String oldImageUrl, MultipartFile newFile, Long userId) {
        String fileKey = update(oldImageUrl, newFile, "post", userId);
        return new PostImage(fileKey);
    }

    public String update(String oldImageUrl, MultipartFile newFile, String prefix, Long id) {
        delete(oldImageUrl);

        if (newFile == null || newFile.isEmpty()) {
            return null;
        }
        String fileKey = uploadFile(newFile, prefix, id);
        return fileKey;
    }

    // ============= 임시 이미지 이동 (프로필, 게시글 사진) ===============
    // 회원가입 전 임시로 저장한 파일의 디렉토리를 /profile/{userId}로 변경함.
    public ProfileImage moveTempToProfile(String tempImageUrl, Integer userId) {
        if (tempImageUrl == null || tempImageUrl.isBlank()) return null;

        // DB에서 임시 프로필 이미지 있는 지 찾기
        // URL로 왔을 경우, 상대 주소로 바꾸고 공통으로 붙는 /public/을 지워 fileKey 로 만든다.
        String oldFileKey = FileUtil.toFileKey(tempImageUrl);

        ProfileImage file = profileImageRepository.findByFileKey(oldFileKey)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "임시 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String newFileKey = moveFile(oldFileKey, "profile", Long.valueOf(userId));

        // DB 엔티티 정보 업데이트
        file.updateFileKey(newFileKey);

        // JPA의 영속성 컨텍스트 변경 감지(Dirty Checking)로 자동 업데이트 됩니다.
        return file;
    }

    public PostImage moveTempToPost(String tempImageUrl, Long postId) {
        if (tempImageUrl == null || tempImageUrl.isBlank()) return null;

        String oldFileKey = FileUtil.toFileKey(tempImageUrl);

        PostImage file = postImageRepository.findByFileKey(oldFileKey)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "임시 이미지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String newFileKey = moveFile(oldFileKey, "post", postId);

        // DB 엔티티 정보 업데이트
        file.updateFileKey(newFileKey);

        return file;
    }

    private String moveFile(String oldFileKey, String newPrefix, Long targetId) {
        String filename = oldFileKey.substring(oldFileKey.lastIndexOf('/') + 1);
        String newFileKey = newPrefix + "/" + targetId + "/" + filename;

        if (isS3Storage()) {
            try {
                s3Client.copyObject(CopyObjectRequest.builder()
                        .copySource(bucketName + "/" + oldFileKey)
                        .destinationBucket(bucketName)
                        .destinationKey(newFileKey)
                        .build());
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(oldFileKey)
                        .build());
                return newFileKey;
            } catch (S3Exception e) {
                log.error("S3 파일 이동 실패: {} -> {}", oldFileKey, newFileKey, e);
                throw new BusinessException("INTERNAL_SERVER_ERROR", "파일 이동에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 로컬 저장소의 현재 임시 파일 저장 경로 계산
        Path oldPath = FILE_DIR.resolve(oldFileKey).normalize();
        
        // 경로 변경할 위치 계산
        String newDirectory = newPrefix + "/" + targetId;
        Path newDirectoryPath = FILE_DIR.resolve(newDirectory);
        Path newPath = newDirectoryPath.resolve(oldPath.getFileName()); // 파일명은 그대로 유지

        // 파일 실제 저장 경로 변경
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
        
        return newDirectory + "/" + oldPath.getFileName().toString();
    }

    private void uploadToS3(MultipartFile file, String fileKey) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException | S3Exception e) {
            log.error("S3 파일 업로드 실패: bucket={}, key={}", bucketName, fileKey, e);
            throw new BusinessException("INTERNAL_SERVER_ERROR", "파일 저장을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isS3Storage() {
        if (!"s3".equalsIgnoreCase(storageType)) {
            return false;
        }
        if (s3Client == null || bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("S3 저장소 설정이 올바르지 않습니다.");
        }
        return true;
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
