package com.example.targetrecruiting.user.repository;

import com.example.targetrecruiting.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //이메일 중복확인
    boolean existsByEmail(String email);

    boolean existsByPhoneNum(String phoneNum);

    Optional<User> findByPhoneNum(String phoneNum);

    Optional<User> findByEmail(String email);
}
