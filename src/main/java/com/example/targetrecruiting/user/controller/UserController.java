package com.example.targetrecruiting.user.controller;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.user.dto.LoginRequestDto;
import com.example.targetrecruiting.user.dto.SignupRequestDto;
import com.example.targetrecruiting.user.dto.UpdateUserRequestDto;
import com.example.targetrecruiting.user.dto.UserDto;
import com.example.targetrecruiting.user.entity.User;
import com.example.targetrecruiting.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseDto<UserDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto){
        return userService.signup(signupRequestDto);
    }
    //이메일 중복확인
    @GetMapping("/check-email")
    public ResponseDto<Boolean> checkEmail(@RequestParam String email){
        return userService.checkEmail(email);
    }

    //로그인
    @PostMapping("/login")
    public ResponseDto<UserDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return userService.login(loginRequestDto);
    }

    //회원조회
    @GetMapping("/{id}")
    public ResponseDto<UserDto> getUser(@PathVariable Long id){
        return userService.getUser(id);
    }

    //회원수정
    @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<UserDto> updateUser(@PathVariable Long id,
                                           @RequestPart(name = "phoneNum") UpdateUserRequestDto updateUserRequestDto,
                                           @RequestPart(name = "profileImage", required = false)MultipartFile image) throws IOException {
        return userService.updateUser(id, updateUserRequestDto, image, new User());
    }
}
