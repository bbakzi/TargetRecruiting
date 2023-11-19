package com.example.targetrecruiting.user.service;

import com.example.targetrecruiting.common.dto.ResponseDto;
import com.example.targetrecruiting.common.jtw.JwtUtil;
import com.example.targetrecruiting.common.util.S3Service;
import com.example.targetrecruiting.user.dto.*;
import com.example.targetrecruiting.user.entity.User;
import com.example.targetrecruiting.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //정규식
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$";
    private static final String PHONENUMS_PATTERN = "^[0-9]{11}$";

    //회원가입
    public ResponseDto<UserDto> signup(SignupRequestDto signupRequestDto) {
        validateEmail(signupRequestDto.getEmail());
        validatePassword(signupRequestDto.getPassword());
        validateNums(signupRequestDto.getPhoneNum());

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        Optional<User> findPhoneNumByEmail = userRepository.findByPhoneNum(signupRequestDto.getEmail());
        if (findPhoneNumByEmail.isPresent()){
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
    public ResponseDto<UserDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response){
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        //이메일 패턴 검사
        validateEmail(email);

        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }

        jwtUtil.createAndSetToken(response, user.getEmail(), user.getId());
        return ResponseDto.setSuccess(HttpStatus.OK,"로그인 성공");
    }

    //회원조회
    public ResponseDto<UserDto> getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        UserDto userDto =new UserDto(user.getId(), user.getEmail(), user.getPhoneNum(), user.getProfileImage());

        return ResponseDto.setSuccess(HttpStatus.OK,"회원 조회 성공",userDto);
    }

    //회원정보수정
    public ResponseDto<UserDto> updateUser(Long id, UpdateUserRequestDto updateUserRequestDto ,
                                           MultipartFile image, User user) throws IOException {
        if (!Objects.equals(id, user.getId())){
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        if (!StringUtils.equals(updateUserRequestDto.getPhoneNum(), user.getPhoneNum())
                                && userRepository.existsByPhoneNum(updateUserRequestDto.getPhoneNum())){
            throw new IllegalArgumentException("사용중인 전화번호 입니다.");
        }
        validateNums(updateUserRequestDto.getPhoneNum());

        String imageUrl = null;

        if (image == null){
            imageUrl = user.getProfileImage();
        }
        if (image != null){
            imageUrl = s3Service.uploadFile(image);
        }
        user.updateUser(updateUserRequestDto,imageUrl);
            userRepository.save(user);

            return ResponseDto.setSuccess(HttpStatus.OK, "회원정보 수정 성공!");
        }
    //비밀번호 변경
    public ResponseDto<UserDto> updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto, User user){
        if (!Objects.equals(id,user.getId())){
            throw new IllegalArgumentException("비밀번호 변경 권한이 없습니다.");
        }
        if (passwordEncoder.matches(updatePasswordRequestDto.getOldPassword(), user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치 하지 않습니다.");
        }
        if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckNewPassword())){
            throw new IllegalArgumentException("두개의 비밀번호가 일치하지 않습니다.");
        }

        validatePassword(updatePasswordRequestDto.getNewPassword());

        user.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        return ResponseDto.setSuccess(HttpStatus.OK,"비밀번호 변경 완료");
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
