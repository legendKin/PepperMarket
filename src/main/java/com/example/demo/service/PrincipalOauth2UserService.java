package com.example.demo.service;

import com.example.demo.entity.PrincipalDetails;
import com.example.demo.entity.Users;
import com.example.demo.oauth.GoogleUserInfo;
import com.example.demo.oauth.NaverUserInfo;
import com.example.demo.oauth.OAuth2UserInfo;
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

        OAuth2UserInfo userInfo;
        if (provider.equals("google")) {
            userInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            userInfo = new NaverUserInfo(response);
        } else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        return handleLogin(oAuth2User, userInfo);
    }

    /**
     * 공통 로그인 처리를 담당하는 메서드입니다.
     * @param oAuth2User OAuth2 사용자 정보
     * @param userInfo OAuth2 사용자 정보 인터페이스
     * @return PrincipalDetails 객체
     */
    private OAuth2User handleLogin(OAuth2User oAuth2User, OAuth2UserInfo userInfo) {
        String provider = userInfo.getProvider();
        String providerId = userInfo.getProviderId();
        String email = userInfo.getEmail();
        String socialId = provider + "_" + providerId;
        String nickname = email.split("@")[0]; // 기본 닉네임은 이메일의 로컬 파트
        String profilePictureUrl = userInfo.getProfilePictureUrl();

        // 사용자 엔티티 생성 또는 업데이트
        Users user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, profilePictureUrl))
                .orElseGet(() -> createNewUser(email, provider, providerId, socialId, nickname, profilePictureUrl));

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    /**
     * 기존 사용자의 정보를 업데이트합니다.
     * @param user 기존 사용자
     * @param profilePictureUrl 프로필 사진 URL
     * @return 업데이트된 사용자
     */
    private Users updateExistingUser(Users user, String profilePictureUrl) {
        user.setProfilePictureUrl(profilePictureUrl);
        return userRepository.save(user);
    }

    /**
     * 새로운 사용자를 생성합니다.
     * @param email 이메일
     * @param provider 제공자
     * @param providerId 제공자 ID
     * @param socialId 소셜 ID
     * @param nickname 닉네임
     * @param profilePictureUrl 프로필 사진 URL
     * @return 생성된 사용자
     */
    private Users createNewUser(String email, String provider, String providerId, String socialId, String nickname, String profilePictureUrl) {
        Users user = Users.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode("")) // 빈 문자열을 암호화하여 설정
                .provider(provider)
                .providerId(providerId)
                .socialId(socialId)
                .nickname(nickname)
                .profilePictureUrl(profilePictureUrl)
                .build();
        return userRepository.save(user);
    }
}
