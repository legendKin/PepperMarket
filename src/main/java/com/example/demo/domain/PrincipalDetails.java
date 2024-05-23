package com.example.demo.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private Users users;
    private Map<String, Object> attributes;

    public PrincipalDetails(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    //살아있는지물어봄 (=생존하고 있는 id인지? 휴면id면 false)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public String getName() {
        return users.getEmail();  // Or any other unique identifier
    }

    public PrincipalDetails(Users users, Map<String, Object> attributes) {
        this.users = users;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return attributes; //Map으로 받는 이유: 이메일, 프로필이 value값이라서
    }



}


//Collection<? extends GrantedAuthority> getAuthorities()
//: 사용자가 가지고 있는 권한 목록 반환
//
//String getPassword()
//: 사용자 비밀번호 반환, 암호화하여 저장해야 함
//
//String getUsername()
//: 사용자 식별 가능한 이름 반환, 고유한 이름이어야 함
//
//boolean isAccountNonExpired()
//: 계정 만료 확인, 만료되지 않은 값: true, 만료된 값: false
//
//boolean isAccountNonLocked()
//: 계정 잠금 확인, 잠금되지 않은 값: true, 잠금된 값: flase
//
//boolean isCredentialsNonExpired()
//: 비밀번호 만료 확인, 만료되지 않은 값: true, 만료된 값: false
//
//boolean isEnabled()
//: 계정 사용 확인, 사용 가능: true, 사용 불가능: false