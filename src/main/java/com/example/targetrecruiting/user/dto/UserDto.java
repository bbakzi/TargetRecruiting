package com.example.targetrecruiting.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private final Long id;

    private final String email;

    private final String phoneNum;

    private final String profileImage;
}
