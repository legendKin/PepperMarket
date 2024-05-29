package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 정보를 담는 엔티티 클래스입니다.
 */
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id; // 사용자 식별자

    @Column(name = "email", nullable = false, unique = true)
    private String email; // 이메일 주소

    @Column(name = "password")
    private String password; // 비밀번호

    @Column(name = "provider")
    private String provider; // 제공자 (OAuth 등)

    @Column(name = "providerId")
    private String providerId; // 제공자 ID (OAuth 등)

    @Column(name = "socialId")
    private String socialId; // 소셜 ID (OAuth 등)

    @Column(name = "nickname")
    private String nickname; // 닉네임

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // 프로필 사진 URL



}
