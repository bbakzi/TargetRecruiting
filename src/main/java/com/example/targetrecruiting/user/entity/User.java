package com.example.targetrecruiting.user.entity;

import com.example.targetrecruiting.user.dto.SignupRequestDto;
import com.example.targetrecruiting.user.dto.UpdatePasswordRequestDto;
import com.example.targetrecruiting.user.dto.UpdateUserRequestDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNum;

    @Column
    private String profileImage;

    public User(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.phoneNum = signupRequestDto.getPhoneNum();
        this.profileImage = signupRequestDto.getProfileImage();
    }

    public void updateUser(UpdateUserRequestDto updateUserRequestDto, @Nullable String imageUrl){
        this.phoneNum = updateUserRequestDto.getPhoneNum();
        this.profileImage = imageUrl;
    }

    public void updatePassword(String newPassword){
        this.password = newPassword;
    }
}
