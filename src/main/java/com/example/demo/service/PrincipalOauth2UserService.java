package com.example.demo.service;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.domain.Users;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // OAuth2 provider 정보
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // Google 인증 처리
        if (provider.equals("google")) {
            return handleGoogleLogin(oAuth2User);
        }
        // Naver 인증 처리
        else if (provider.equals("naver")) {
            return handleNaverLogin(oAuth2User);
        }

        throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
    }

    private OAuth2User handleGoogleLogin(OAuth2User oAuth2User) {
        // 구글 유저 id
        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");

        // provider_providerId 형식으로 저장해 중복 방지
        String socialId = "google_" + providerId;

        Optional<Users> optionalUsers = userRepository.findByEmail(email);
        Users users = optionalUsers.orElseGet(() -> {
            Users newUser = Users.builder()
                    .email(email)
                    .provider("google")
                    .providerId(providerId)
                    .socialId(socialId)
                    .build();
            return userRepository.save(newUser);
        });

        return new PrincipalDetails(users, oAuth2User.getAttributes());
    }

    private OAuth2User handleNaverLogin(OAuth2User oAuth2User) {
        // 네이버 사용자 정보 가져오기
        NaverUserInfo naverUserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        String providerId = naverUserInfo.getProviderId();
        String email = naverUserInfo.getEmail();

        // provider_providerId 형식으로 저장해 중복 방지
        String socialId = "naver_" + providerId;

        Optional<Users> optionalUsers = userRepository.findByEmail(email);
        Users users = optionalUsers.orElseGet(() -> {
            Users newUser = Users.builder()
                    .email(email)
                    .provider("naver")
                    .providerId(providerId)
                    .socialId(socialId)
                    .build();
            return userRepository.save(newUser);
        });

        return new PrincipalDetails(users, oAuth2User.getAttributes());
    }
}
