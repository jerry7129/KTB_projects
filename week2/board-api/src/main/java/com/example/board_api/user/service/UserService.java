package com.example.board_api.user.service;

import com.example.board_api.auth.repository.RefreshTokenRepository;
import com.example.board_api.file.repository.ProfileImageRepository;
import com.example.board_api.file.domain.entity.ProfileImage;
import com.example.board_api.file.service.FileService;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.global.util.FileUtil;
import com.example.board_api.user.controller.dto.response.UserInfoResponseDto;
import com.example.board_api.user.controller.dto.response.UserSignupResponseDto;
import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.controller.dto.request.UserRequestDto;
import com.example.board_api.user.repository.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Service Layer는 직접적인 비즈니스 로직을 펼치지 않고
// Domain과 Repository의 구현체를 컨트롤한다.
// 비즈니스 로직 인터페이스와 엔티티는 Domain에 있음.
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원 가입 로직
    @Transactional
    public UserSignupResponseDto createUser(UserRequestDto.SignUp request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("USER_EMAIL_CONFLICT", "중복된 이메일 입니다.", HttpStatus.CONFLICT);
        } else if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        // POST /users/temp-profile-image 요청을 통해 얻은 임시 저장소 URL을 바탕으로
        // POST /users 로 유저 생성 이후에 임시 저장소의 프로필 이미지를 profile/{userId}/{fileName} 으로 옮김.
        // dirty check 로 유저 엔티티 변동이 자동으로 DB 반영됨
        if (request.getProfileImageUrl() != null
                && !request.getProfileImageUrl().isBlank()
                // 프로필 이미지가 default-profile.png 로 설정되어 있어도 무시.
                && !request.getProfileImageUrl().contains("default-profile.png")) {
            ProfileImage movedFile = fileService.moveTempToProfile(request.getProfileImageUrl(), savedUser.getId());
            savedUser.changeProfileImage(movedFile); // 유저 엔티티에 프로필 연결
        }

        return new UserSignupResponseDto(savedUser.getEmail(), savedUser.getCreatedAt().toString());
    }

    @Transactional
    public UserInfoResponseDto getUser(Integer userId) {
        // userId를 지닌 유저가 있는 지 체크, 동시에 images의 Object key 값도 가져옴.
        User user = userRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserInfoResponseDto.from(user);
    }

    // 유저의 닉네임 또는 프로필 이미지를 변경하는 함수
    // 프로필 이미지 업데이트의 경우,
    // 1. 디스크의 물리 이미지 삭제 -> 2. 새로운 이미지 업로드
    @Transactional
    public UserInfoResponseDto updateUserInfo(
            Integer userId,
            UserRequestDto.UpdateInfo requestDto,
            MultipartFile profileImage
        ) {
        // userId를 지닌 유저가 있는 지 체크
        User user = userRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        // 변경하려는 nickname을 갖고있는 유저가 있는 지 체크
        if(!user.getNickname().equals(requestDto.getNickname()) &&
                userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        // 이미지를 디스크에 저장 - 현재는 local 서버의 /uploads 폴더에 저장 중
        // 변경 시, 기존에 가지고 있던 모든 프로필 이미지의 물리 파일을 삭제. (고아 파일 방지)
        if (user.getProfileImages() != null && !user.getProfileImages().isEmpty()) {
            for (ProfileImage oldImage : user.getProfileImages()) {
                fileService.delete(oldImage.getFileKey());
            }
        }

        ProfileImage newImage = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            // update할 profileImage를 /uploads/{userId} 에 업로드
            newImage = fileService.uploadProfileImage(profileImage, user.getId());
        }

        // JPA의 dirty check를 사용. Transaction 종료 후에 자동으로 DB에 commit 됨.
        // changeUserInformation 안에서 profileImages.clear()가 호출되어 기존 DB 데이터가 고아 객체로 지워짐.
        user.changeUserInformation(requestDto.getNickname(), newImage);
        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public void updateUserNickname(Integer userId, UserRequestDto.UpdateInfo requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        // 변경하려는 nickname을 갖고있는 유저가 있는 지 체크
        if(!user.getNickname().equals(requestDto.getNickname()) &&
                userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        user.changeUserNickname(requestDto.getNickname());
    }

    @Transactional
    public void updateUserPassword(Integer userId, UserRequestDto.UpdatePassword requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        user.changePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        // 일단 삭제하려는 유저와 연관된 이미지(프로필 사진, 유저가 작성한 게시글의 이미지)의 이미지 키 리스트를 조회
        List<String> targetImageKeys = userRepository.findByUserIdWidthAllImageKeys(userId);
        // 각 이미지 키를 바탕으로 실제 물리 파일을 삭제
        // 현재 로컬 디스크에 저장하는 경우 파일 삭제는 잘 되지만, 파일 경로에 생긴 폴더는 삭제가 안됨.
        // 향후에 해결 예결
        for(String fileKey : targetImageKeys) {
            fileService.delete(fileKey);
        }

        // 사용자의 refresh_token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // DB table에서 유저와 연관된 데이터를 일괄 삭제함.
        userRepository.deleteByIdWithProfileImageWithPost(userId);
    }


    // ========== Private Methods ==========
    private ProfileImage resolveProfileImage(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }

        // 1. 전체 URL에서 도메인을 떼고 상대 경로만 추출
        String relativePath = FileUtil.extractPathFromUrl(profileImageUrl);
        String fileKey = relativePath.replaceFirst("^/?public/", "");

        // 2. 추출된 상대 경로로 DB 조회 -> 키로 바꿈.
        return profileImageRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));
    }
}
