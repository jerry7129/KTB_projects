package com.example.board_api.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "{user.email.not-blank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.password.not-blank}")
    @Size(min = 8, max = 20, message = "{user.password.size}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$",
            message = "{user.password.pattern}")
    private String password;
}
