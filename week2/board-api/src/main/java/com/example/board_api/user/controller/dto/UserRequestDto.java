package com.example.board_api.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "{user.email.not-blank}")
    private String email;
    @NotBlank(message = "{user.password.not-blank}")
    @Size(min = 8, max = 20, message = "{user.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$",
            message = "{user.password.pattern}")
    private String password;
    @NotBlank(message = "{user.nickname.not-blank}")
    private String nickname;
//    @Pattern(
//            regexp = "^.*\\.(jpg|jpeg|png|gif)$",
//            message = "{user.profile-image.pattern}"
//    )
    private String profileImageURL;
}
