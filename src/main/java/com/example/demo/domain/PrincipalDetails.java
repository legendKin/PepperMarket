package com.example.demo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private Users users;     // 사용자 정보를 담은 Users 객체
    private Map<String, Object> attributes;  // OAuth2 사용자의 속성을 담은 Map 객체

    // Users 객체를 받아와서 PrincipalDetails 객체 생성하는 생성자
    public PrincipalDetails(Users users) {
        this.users = users;
    }
    // 사용자의 권한을 부여하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user")); // 기본적으로 "user" 권한을 가짐
    }

    // 사용자의 비밀번호를 반환하는 메서드
    @Override
    public String getPassword() {
        return users.getPassword(); // Users 객체의 비밀번호 반환
    }

    // 사용자의 이름(여기서는 이메일)을 반환하는 메서드
    @Override
    public String getUsername() {
        return users.getEmail(); // Users 객체의 이메일 반환
    }

    // 사용자 계정의 만료 여부를 반환하는 메서드
    @Override
    public boolean isAccountNonExpired() {
        return true; // 사용자 계정 만료 여부는 항상 true로 설정
    }

    // 사용자 계정의 잠금 여부를 반환하는 메서드
    @Override
    public boolean isAccountNonLocked() {
        return true; // 사용자 계정 잠금 여부는 항상 true로 설정
    }

    // 사용자 자격 증명(비밀번호 등)의 만료 여부를 반환하는 메서드
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 사용자 자격 증명의 만료 여부는 항상 true로 설정
    }

    // 사용자 계정의 활성화 여부를 반환하는 메서드
    @Override
    public boolean isEnabled() {
        return true; // 사용자 계정 활성화 여부는 항상 true로 설정
    }

    // OAuth2 사용자의 이름을 반환하는 메서드
    @Override
    public String getName() {
        return users.getEmail(); // OAuth2 사용자의 이름으로도 이메일을 사용
    }

    public PrincipalDetails(Users users, Map<String, Object> attributes) {
        this.users = users;
        this.attributes = attributes;
    }

    // OAuth2 사용자의 속성을 반환하는 메서드
    @Override
    public Map<String, Object> getAttributes() {
        return attributes; // OAuth2 사용자의 속성 반환
    }

    // OAuth2 사용자의 프로필 사진 URL을 반환하는 메서드
    public String getProfilePictureUrl() {
        // 프로필 사진 URL은 OAuth2 사용자의 속성(attribute)으로 추가되었으므로,
        // 여기서 해당 속성을 가져와서 반환합니다.
        return (String) attributes.get("profile_picture_url");
    }
}