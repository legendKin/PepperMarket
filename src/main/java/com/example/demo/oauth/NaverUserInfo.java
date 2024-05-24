package com.example.demo.oauth;


import lombok.AllArgsConstructor;
import java.util.Map;


@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes; // Naver 사용자 정보를 담는 맵

    @Override
    public String getProviderId() {
        return (String) attributes.get("response.id"); // Naver 사용자 ID를 반환
    }

    @Override
    public String getProvider() {
        return "naver"; // 제공자 이름을 반환 (Naver)
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email"); // 이메일을 반환
    }

    @Override
    public String getName() {
        return (String) attributes.get("name"); // 이름을 반환
    }

    // 프로필 사진 URL 반환
    public String getProfilePictureUrl() {

        return (String) attributes.get("profile_image"); // 프로필 사진 URL을 반환
    }
}
