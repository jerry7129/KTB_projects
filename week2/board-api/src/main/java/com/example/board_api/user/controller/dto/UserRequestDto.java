package com.example.board_api.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "이메일을 입력해야 합니다.")
    private String email;
    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    @Pattern(regexp = "/^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$/;",
            message = "대문자, 소문자, 숫자, 특수문자(!@#$%^&*?_)를 각각 최소 1개 포함해야 합니다.")
    private String password;
    @NotBlank(message = "닉네임을 입력해야 합니다.")
    private String nickname;
    private String profileImage;
}
