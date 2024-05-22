package com.example.demo.service;


import com.example.demo.domain.PrincipalDetails;
import com.example.demo.domain.Users;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j //로그 남기는 어노테이션 (구글에서 넘어오는 값을 로그로 남기기)
//DefaultOAuth2UserService를 상속받아 회원가입 (첫 로그인이면 회원가입도 동시에)
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    //userRequest, OAuth2User로 필요한 정보를 추출해 가입 진행
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttirbutes : {}", oAuth2User.getAttributes());

        //구글
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 구글 유저 id
        String provider_id = oAuth2User.getAttribute("sub");

        //provider_providerid형식으로 저장해 중복 방지
        String email = oAuth2User.getAttribute("email");

        //구글_구글id
        String social_id = provider + "_" + provider_id;

        Optional<Users> optionalUsers = userRepository.findByEmail(email);
        Users users = null;

        if(optionalUsers.isEmpty()) {
            users = Users.builder()
                    .email(email)
                    .provider(provider)
                    .provider(provider_id)
                    .social_id(social_id)
                    .build();
            userRepository.save(users);
        } else {
            users = optionalUsers.get();
        }

        return new PrincipalDetails(users, oAuth2User.getAttributes());
    }
}