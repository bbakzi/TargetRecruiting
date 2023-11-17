package com.example.targetrecruiting.user.controller;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.user.dto.SignupRequestDto;
import com.example.targetrecruiting.user.dto.UserDto;
import com.example.targetrecruiting.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    //로그아웃
    //회원수정
}
