package com.example.demo.oauth;

// OAuth2 사용자 정보 인터페이스
public interface OAuth2UserInfo {
    // 제공자로부터 사용자 ID를 가져오는 메서드
    String getProviderId();

    // 제공자 이름을 가져오는 메서드
    String getProvider();

    // 이메일을 가져오는 메서드
    String getEmail();

    // 이름을 가져오는 메서드
    String getName();

    // 프로필 사진 URL을 가져오는 메서드
    String getProfilePictureUrl();
}
