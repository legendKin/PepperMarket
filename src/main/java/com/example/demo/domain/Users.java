package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter

public class Users{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "provider")
    private String provider;

    @Column(name = "providerId")
    private String providerId;

    @Column(name = "socialId")
    private String socialId;

    @Column(name = "nickname")
    private String nickname;


    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // 프로필 사진을 저장하는 필드 추가
}