package com.example.demo.service;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.domain.Users;
import com.example.demo.oauth.GoogleUserInfo;
import com.example.demo.oauth.NaverUserInfo;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 사용자 정보를 가져와서 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository; // 사용자 레포지토리
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화 인코더

    /**
     * OAuth2 사용자 정보를 가져오는 메서드입니다.
     * @param userRequest OAuth2 사용자 요청
     * @return OAuth2 사용자 정보
     * @throws OAuth2AuthenticationException OAuth2 인증 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // 부모 클래스의 메서드 호출
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if (provider.equals("google")) {
            return handleGoogleLogin(oAuth2User);
        } else if (provider.equals("naver")) {
            return handleNaverLogin(oAuth2User);
        }

        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }

    /**
     * 구글 로그인 정보를 처리하는 메서드입니다.
     * @param oAuth2User OAuth2 사용자 정보
     * @return PrincipalDetails 객체
     */
    private OAuth2User handleGoogleLogin(OAuth2User oAuth2User) {
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        // 프로필 사진 URL 가져오기
        String profilePictureUrl = googleUserInfo.getProfilePictureUrl();

        // 나머지 사용자 정보 가져오기
        String providerId = googleUserInfo.getProviderId();
        String email = googleUserInfo.getEmail();
        String socialId = "google_" + providerId;
        String nickname = email.split("@")[0]; // 기본 닉네임은 이메일의 로컬 파트

        // 사용자 엔티티 생성
        Users newUser = Users.builder()
                .email(email)
                .provider("google")
                .providerId(providerId)
                .socialId(socialId)
                .nickname(nickname)
                .profilePictureUrl(profilePictureUrl) // 프로필 사진 URL 추가
                .build();

        // 사용자 저장 또는 업데이트
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        Users savedUser;
        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            // 기존 사용자 업데이트
            existingUser.setProfilePictureUrl(profilePictureUrl); // 프로필 사진 URL 업데이트
            savedUser = userRepository.save(existingUser);
        } else {
            // 새로운 사용자 저장
            savedUser = userRepository.save(newUser);
        }

        return new PrincipalDetails(savedUser, oAuth2User.getAttributes());
    }

    /**
     * 네이버 로그인 정보를 처리하는 메서드입니다.
     * @param oAuth2User OAuth2 사용자 정보
     * @return PrincipalDetails 객체
     */
    private OAuth2User handleNaverLogin(OAuth2User oAuth2User) {
        NaverUserInfo naverUserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));

        // 프로필 사진 URL 가져오기
        String profilePictureUrl = naverUserInfo.getProfilePictureUrl();

        // 나머지 사용자 정보 가져오기
        String providerId = naverUserInfo.getProviderId();
        String email = naverUserInfo.getEmail();
        String socialId = "naver_" + providerId;
        String nickname = email.split("@")[0]; // 기본 닉네임은 이메일의 로컬 파트

        // 사용자 엔티티 생성
        Users newUser = Users.builder()
                .email(email)
                .provider("naver")
                .providerId(providerId)
                .socialId(socialId)
                .nickname(nickname)
                .profilePictureUrl(profilePictureUrl) // 프로필 사진 URL 추가
                .build();

        // 사용자 저장 또는 업데이트
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        Users savedUser;
        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            // 기존 사용자 업데이트
            existingUser.setProfilePictureUrl(profilePictureUrl); // 프로필 사진 URL 업데이트
            savedUser = userRepository.save(existingUser);
        } else {
            // 새로운 사용자 저장
            savedUser = userRepository.save(newUser);
        }

        return new PrincipalDetails(savedUser, oAuth2User.getAttributes());
    }
}
