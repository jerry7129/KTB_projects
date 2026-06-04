package com.example.board_api.user.service;

import com.example.board_api.file.domain.FileRepository;
import com.example.board_api.file.domain.entity.File;
import com.example.board_api.file.service.FileService;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.global.util.FileUtil;
import com.example.board_api.user.controller.dto.response.UserInfoResponseDto;
import com.example.board_api.user.controller.dto.response.UserSignupResponseDto;
import com.example.board_api.user.domain.UserQueryRepository;
import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

// Service Layer는 직접적인 비즈니스 로직을 펼치지 않고
// Domain과 Repository의 구현체를 컨트롤한다.
// 비즈니스 로직 인터페이스와 엔티티는 Domain에 있음.
@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;

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
        // dirty checking 으로 유저 엔티티 변동이 자동으로 DB 반영됨
        if (request.getProfileImageUrl() != null
                && !request.getProfileImageUrl().isBlank()
                // 프로필 이미지가 default-profile.png 로 설정되어 있어도 무시.
                && !request.getProfileImageUrl().contains("default-profile.png")) {
            File movedFile = fileService.moveTempToProfile(request.getProfileImageUrl(), savedUser.getId());
            savedUser.changeProfileImage(movedFile); // 유저 엔티티에 프로필 연결
        }

        return new UserSignupResponseDto(savedUser.getEmail(), savedUser.getCreatedAt().toString());
    }

    @Transactional
    public UserInfoResponseDto getUser(Long userId) {
        // userId를 지닌 유저가 있는 지 체크, 동시에 images의 Object key 값도 가져옴.
        User user = userQueryRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public UserInfoResponseDto updateUserInfo(
            Long userId,
            UserRequestDto.UpdateInfo requestDto,
            MultipartFile profileImage
        ) {
        // userId를 지닌 유저가 있는 지 체크
        User user = userQueryRepository.findByIdWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        // 변경하려는 nickname을 갖고있는 유저가 있는 지 체크
        if(!user.getNickname().equals(requestDto.getNickname()) &&
                userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        // 이미지를 disk에 저장 - 현재는 local 서버의 /uploads 폴더에 저장 중
        // 저장 후, 저장 위치 URL을 return함. 주소가 없을 경우 default 주소를 return함.
        String oldFileKey = user.getProfileImage().isEmpty() ? null : user.getProfileImage().getLast().getFileKey();
        File newImage = fileService.updateProfileImage(oldFileKey, profileImage, user.getId());

        // JPA의 Dirty Check를 사용. 객체를 수정하기만 해도 Transaction 종료 후에 자동으로 DB에 commit 됨.
        // 이때 profileImage가 빈 값이면 imageUrl은 null이 되고, user의 프로필 사진 주소는 변경되지 않는다.
        user.changeUserInformation(requestDto.getNickname(), newImage);
        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public void updateUserNickname(Long userId, UserRequestDto.UpdateInfo requestDto) {
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
    public void updateUserPassword(Long userId, UserRequestDto.UpdatePassword requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        user.changePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }


    // ========== Private Methods ==========
    private File resolveProfileImage(String profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isBlank()) {
            return null;
        }

        // 1. 전체 URL에서 도메인을 떼고 상대 경로만 추출
        String relativePath = FileUtil.extractPathFromUrl(profileImageUrl);
        String fileKey = relativePath.replaceFirst("^/?public/", "");

        // 2. 추출된 상대 경로로 DB 조회 -> 키로 바꿔야함.
        return fileRepository.findByFileKey(fileKey)
                .orElseThrow(() -> new NotFoundException("PROFILE_IMAGE_NOT_FOUND"));
    }
}
