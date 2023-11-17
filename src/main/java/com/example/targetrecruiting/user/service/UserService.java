package com.example.targetrecruiting.user.service;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.user.dto.LoginRequestDto;
import com.example.targetrecruiting.user.dto.SignupRequestDto;
import com.example.targetrecruiting.user.dto.UserDto;
import com.example.targetrecruiting.user.entity.User;
import com.example.targetrecruiting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional

public class UserService {
    private final UserRepository userRepository;

    //정규식
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$";
    private static final String PHONENUMS_PATTERN = "^[0-9]{11}$";

    //회원가입
    public ResponseDto<UserDto> signup(SignupRequestDto signupRequestDto) {
        validateEmail(signupRequestDto.getEmail());
        validatePassword(signupRequestDto.getPassword());
        validateNums(signupRequestDto.getPhoneNums());

        Optional<User> findPhoneNumsByEmail = userRepository.findByPhoneNum(signupRequestDto.getEmail());
        if (findPhoneNumsByEmail.isPresent()){
            throw new IllegalArgumentException("이미 가입된 번호 입니다.");
        }

        User user = new User(signupRequestDto);
        userRepository.save(user);
        return ResponseDto.setSuccess(HttpStatus.OK, "회원가입 성공!");
    }

    //이메일 중복검사
    public ResponseDto<Boolean> checkEmail(String email){
        validateEmail(email);

        boolean exists = userRepository.existsByEmail(email);
        if(exists){
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        return ResponseDto.setSuccess(HttpStatus.OK,"사용 가능한 이메일입니다.");
    }

    //로그인
    public ResponseDto<UserDto> login(LoginRequestDto loginRequestDto){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        //이메일 패턴 검사
        validateEmail(email);

        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (!password.matches(user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }
        return ResponseDto.setSuccess(HttpStatus.OK,"로그인 성공");
    }

    //이메일 패턴검사
    private void validateEmail(String email){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()){
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }

    }

    //비밀번호 패턴검사
    private void validatePassword(String password){
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()){
            throw new IllegalArgumentException("비밀번호 형식이 올바르지 않습니다.");
        }

    }

    //전화번호 패턴검사
    private void validateNums(String phoneNums){
        Pattern pattern = Pattern.compile(PHONENUMS_PATTERN);
        Matcher matcher = pattern.matcher(phoneNums);
        if (!matcher.matches()){
            throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다.");
        }

    }

}
