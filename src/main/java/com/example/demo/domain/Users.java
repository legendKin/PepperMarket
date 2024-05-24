package com.example.demo.domain;

// 필요한 클래스와 어노테이션을 가져옴
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

// JPA 엔티티를 정의하는 Users 클래스
@Entity
@Builder // 빌더 패턴을 사용하기 위한 어노테이션
@Getter // lombok을 사용하여 getter 메서드 자동 생성
@NoArgsConstructor // lombok을 사용하여 기본 생성자 자동 생성
@AllArgsConstructor // lombok을 사용하여 모든 필드를 인수로 하는 생성자 자동 생성
@Setter // lombok을 사용하여 setter 메서드 자동 생성
public class Users {

    // 엔티티의 주요 키를 정의하는 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 사용자의 이메일을 저장하는 필드
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // 사용자의 비밀번호를 저장하는 필드
    @Column(name = "password")
    private String password;

    // OAuth2 로그인의 제공자를 저장하는 필드
    @Column(name = "provider")
    private String provider;

    // OAuth2 로그인의 제공자 ID를 저장하는 필드
    @Column(name = "providerId")
    private String providerId;

    // 소셜 로그인의 사용자 ID를 저장하는 필드
    @Column(name = "socialId")
    private String socialId;

    // 사용자의 별명을 저장하는 필드
    @Column(name = "nickname")
    private String nickname;

    // 사용자의 프로필 사진 URL을 저장하는 필드
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
}
