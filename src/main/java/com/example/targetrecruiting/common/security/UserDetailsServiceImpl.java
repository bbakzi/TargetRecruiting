package com.example.targetrecruiting.common.security;

import com.example.targetrecruiting.user.entity.User;
import com.example.targetrecruiting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NullPointerException("유저를 찾을 수 없습니다")
        );
        return new UserDetailsImpl(user);
    }
}
