package com.example.board_api.user.service;

import com.example.board_api.file.FileService;
import com.example.board_api.global.exception.BusinessException;
import com.example.board_api.global.exception.NotFoundException;
import com.example.board_api.user.domain.UserRole;
import com.example.board_api.user.domain.UserStatus;
import com.example.board_api.user.controller.dto.UserRequestDto;
import com.example.board_api.user.controller.dto.UserResponseDto;
import com.example.board_api.user.domain.UserRepository;
import com.example.board_api.user.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

// Service Layer는 직접적인 비즈니스 로직을 펼치지 않고
// Domain과 Repository의 구현체를 컨트롤한다.
// 비즈니스 로직 인터페이스와 엔티티는 Domain에 있음.
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       FileService fileService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원 가입 로직
    @Transactional
    public UserResponseDto createUser(UserRequestDto.SignUp request, MultipartFile profileImage) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("USER_EMAIL_CONFLICT", "중복된 이메일 입니다.", HttpStatus.CONFLICT);
        } else if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 프로필 이미지를 /uploads/profile/{userId} 에 저장을 하려고 보니,
        // 유저를 생성 전에 imageUrl 계산을 해야하는데, 유저가 생성되지 않으면 userId를 알 수 없는 문제가 있다.
        // 이를 JPA의 dirty checking으로 해결하였다.
        // 일단 빈 imageUrl로 유저를 생성 한 뒤에, image 업로드를 하고
        // 새로 생긴 user의 imageUrl을 업데이트 하게 했다.
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        User savedUser = userRepository.save(user);

        // 이미지를 disk에 저장 - 현재는 local 서버의 upload 폴더에 저장 중
        // 저장 후, 저장 위치 URL을 return함. profileImage가 없을 경우 그냥 default Url 사용함.
        String imageUrl = fileService.uploadProfileImage(profileImage, savedUser.getId());
        // dirty checking
        if(imageUrl == null || imageUrl.isBlank() || imageUrl.equals(user.getProfileImageUrl())) {
            return new UserResponseDto(savedUser);
        }
        savedUser.changeProfileImageUrl(imageUrl);
        return new UserResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto getUser(Long userId) {
        // userId를 지닌 유저가 있는 지 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        return new UserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUserInfo(
            Long userId,
            UserRequestDto.UpdateInfo requestDto,
            MultipartFile profileImage
        ) {
        // userId를 지닌 유저가 있는 지 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
        // 변경하려는 nickname을 갖고있는 유저가 있는 지 체크
        if(!user.getNickname().equals(requestDto.getNickname()) &&
                userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new BusinessException("USER_NICKNAME_CONFLICT", "중복된 닉네임 입니다.", HttpStatus.CONFLICT);
        }

        // 이미지를 disk에 저장 - 현재는 local 서버의 /uploads 폴더에 저장 중
        // 저장 후, 저장 위치 URL을 return함. 주소가 없을 경우 default 주소를 return함.
        String imageUrl = fileService.updateProfileImage(user.getProfileImageUrl(), profileImage, user.getId());

        // JPA의 Dirty Check를 사용. 객체를 수정하기만 해도 Transaction 종료 후에 자동으로 DB에 commit 됨.
        // 이때 profileImage가 빈 값이면 imageUrl은 null이 되고, user의 프로필 사진 주소는 변경되지 않는다.
        user.changeUserInformation(requestDto.getNickname(), imageUrl);
        return new UserResponseDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
