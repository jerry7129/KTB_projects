package com.example.board_api.user.controller.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// /user 로 들어오는 요청에 대한 Dto를 메소드마다 새로 정의하는 것이 좋다고 했는데,
// 그러면 Dto 개수가 너무 많아져서 static inner class로 하나의 파일에서 관리함.
public class UserRequestDto {

    // 회원가입 요청 Dto
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUp {

        @NotBlank(message = "{user.email.not-blank}")
        @Email(message = "{user.email.email}")
        private String email;

        @NotBlank(message = "{user.password.not-blank}")
        @Size(min = 8, max = 20, message = "{user.password.size}")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$",
                message = "{user.password.pattern}")
        private String password;

        @NotBlank(message = "{user.nickname.not-blank}")
        @Size(min = 2, max = 10, message = "{user.nickname.size}")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]+$",
                message = "{user.nickname.pattern}"
        )
        private String nickname;

        @Pattern(
                regexp = "^.*\\.(jpg|jpeg|png|gif)$",
                message = "{user.profile-image.pattern}"
        )

        private String profileImageUrl;
    }

    // 회원 정보 수정 Dto ( 닉네임 필수, 프로필 사진 선택)
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateInfo {

        @NotBlank(message = "{user.nickname.not-blank}")
        @Size(min = 2, max = 10, message = "{user.nickname.size}")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]+$",
                message = "{user.nickname.pattern}"
        )
        private String nickname;

        @Pattern(
                regexp = "^.*\\.(jpg|jpeg|png|gif)$",
                message = "{user.profile-image.pattern}"
        )
        private String profileImage;
    }

    // 회원 비밀번호 수정 Dto
    @Getter
    public static class UpdatePassword {

        @NotBlank(message = "{user.password.not-blank}")
        @Size(min = 8, max = 20, message = "{user.password.size}")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$",
                message = "{user.password.pattern}")
        private String password;
    }
}
