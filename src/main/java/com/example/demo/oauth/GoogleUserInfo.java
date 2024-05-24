package com.example.demo.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    // 추가 - 프로필 사진 URL 반환
    public String getProfilePictureUrl() {
        // 프로필 사진 URL은 Google에서 제공하는 'picture' 속성에 있을 수 있습니다.
        // 속성이 없는 경우 기본값이나 null을 반환할 수 있습니다.
        return (String) attributes.get("picture");
    }
}