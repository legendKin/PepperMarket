package com.example.demo.oauth;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

// Google 사용자 정보를 나타내는 클래스
@Getter
@Setter
@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes; // Google 사용자 정보를 담는 맵

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub"); // Google 사용자 ID를 반환
    }

    @Override
    public String getProvider() {
        return "google"; // 제공자 이름을 반환 (Google)
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
        // 속성이 없는 경우 기본값이나 null을 반환
        return (String) attributes.get("picture"); // 프로필 사진 URL을 반환
    }
}
