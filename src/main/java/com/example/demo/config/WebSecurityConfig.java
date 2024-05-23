package com.example.demo.config;

import com.example.demo.service.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig  {

    private final PrincipalOauth2UserService principalOauth2UserService;

    // 정적 자원 및 오류 페이지 무시
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/error");
    }

    // 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                // HTTP 요청에 대한 권한 부여 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/login", "/signup", "/user","/board/list","/","/main","/layout","/img/**","/css/**", "/js/**").permitAll() // /login, /signup, /user 경로는 모든 사용자에게 허용
                        .anyRequest().authenticated()) // 다른 요청은 인증된 사용자만 허용

                // 폼 로그인 설정
                .formLogin(formLogin -> {
                    formLogin
                            .loginPage("/login") // 로그인 페이지 지정
                            .usernameParameter("nickname") // 사용자 이름 매개변수 설정
                            .defaultSuccessUrl("/"); // 기본 로그인 성공 후 이동할 페이지 설정
                })

                // 로그아웃 설정
                .logout(logout -> {
                    logout
                            .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 페이지 설정
                            .invalidateHttpSession(true) // HTTP 세션 무효화 여부 설정
                            .deleteCookies("JSESSIONID"); // 쿠키 삭제 설정
                })

                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화

                // OAuth2 로그인 설정
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login") // 로그인 페이지 지정
                        .defaultSuccessUrl("/") // 기본 OAuth2 로그인 성공 후 이동할 페이지 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService) // OAuth2 사용자 정보 엔드포인트 설정
                        )
                )


                .build(); // 보안 필터 체인 빌드
    }
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

}
