package com.example.targetrecruiting.user.entity;

import com.example.targetrecruiting.common.util.TimeStamped;
import com.example.targetrecruiting.recruitingBoard.entity.Board;
import com.example.targetrecruiting.user.dto.SignupRequestDto;
import com.example.targetrecruiting.user.dto.UpdateUserRequestDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "users")
@Getter
@NoArgsConstructor
public class User extends TimeStamped {

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

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Board> boardList;

    public User(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.phoneNum = signupRequestDto.getPhoneNum();
        this.profileImage = signupRequestDto.getProfileImage();
    }

    public User(String password, SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = password;
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
