package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

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
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // 변경: nullable = true
    private String password;

    private String nickname;

    private String profilePictureUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @Column(name = "provider")
    private String provider; // 제공자 (OAuth 등)

    @Column(name = "providerId")
    private String providerId; // 제공자 ID (OAuth 등)

    @Column(name = "socialId")
    private String socialId; // 소셜 ID (OAuth 등)

    @Builder
    public Users(String email, String password, String nickname, String profilePictureUrl, String provider, String providerId, String socialId) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profilePictureUrl = profilePictureUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.socialId = socialId;
    }


    @Column(name = "name")
    private String name; // 이름

    @Column(name = "age")
    private Integer age; // 나이

    @Column(name = "birthdate")
    @Temporal(TemporalType.DATE)
    private Date birthdate; // 생년월일

}
