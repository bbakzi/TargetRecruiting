package com.example.targetrecruiting.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    @Email
    @NotBlank(message = "필수 입력 값입니다.")
    private String email;

    @NotBlank(message = "필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "필수 입력 값입니다.")
    private String phoneNums;

    private String profileImage;

}
