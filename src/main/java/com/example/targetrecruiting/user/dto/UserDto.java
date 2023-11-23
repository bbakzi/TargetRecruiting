package com.example.targetrecruiting.user.dto;

import lombok.Getter;

@Getter
public class UserDto {
    private final Long id;

    private final String email;

    private final String phoneNum;

    private final String profileImage;

    public UserDto(Long id, String email, String phoneNum, String profileImage) {
        this.id = id;
        this.email = email;
        this.phoneNum = phoneNum;
        this.profileImage = profileImage;
    }
}
