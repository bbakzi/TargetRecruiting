package com.example.targetrecruiting.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "users")
@Getter
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
}
