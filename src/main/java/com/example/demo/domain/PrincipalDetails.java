package com.example.demo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 사용자 정보를 담은 클래스입니다.
 */
public class PrincipalDetails implements UserDetails, OAuth2User {

    private Users users;
    private Map<String, Object> attributes;

    /**
     * PrincipalDetails의 생성자입니다.
     *
     * @param users 사용자 정보 객체
     */
    public PrincipalDetails(Users users) {
        this.users = users;
    }

    /**
     * PrincipalDetails의 생성자입니다.
     *
     * @param users      사용자 정보 객체
     * @param attributes OAuth2 사용자의 속성을 담은 맵
     */
    public PrincipalDetails(Users users, Map<String, Object> attributes) {
        this.users = users;
        this.attributes = attributes;
    }

    /**
     * 사용자 권한 목록을 반환합니다.
     *
     * @return 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    /**
     * 사용자 비밀번호를 반환합니다.
     *
     * @return 비밀번호
     */
    @Override
    public String getPassword() {
        return users.getPassword();
    }

    /**
     * 사용자 식별 가능한 이름을 반환합니다.
     *
     * @return 사용자 이름
     */
    @Override
    public String getUsername() {
        return users.getEmail();
    }

    /**
     * 계정의 만료 여부를 반환합니다.
     *
     * @return 계정 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정의 잠금 여부를 반환합니다.
     *
     * @return 계정 잠금 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호)의 만료 여부를 반환합니다.
     *
     * @return 자격 증명 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정의 사용 가능 여부를 반환합니다.
     *
     * @return 계정 사용 가능 여부
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 사용자의 이름(닉네임)을 반환합니다.
     *
     * @return 사용자 이름
     */
    @Override
    public String getName() {
        return users.getNickname();
    }

    /**
     * OAuth2 사용자의 속성을 담은 맵을 반환합니다.
     *
     * @return OAuth2 사용자의 속성을 담은 맵
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 프로필 사진 URL을 반환합니다.
     *
     * @return 프로필 사진 URL
     */
    public String getProfilePic() {
        return (String) attributes.get("profile_picture_url");
    }

    /**
     * 프로필 사진 URL을 반환합니다.
     *
     * @return 프로필 사진 URL
     */
    public String getPPic() {
        return users.getProfilePictureUrl();
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