package com.example.demo.config;

import com.example.demo.service.PrincipalOauth2UserService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {


    private final PrincipalOauth2UserService principalOauth2UserService;

    // 특정 요청을 무시하도록 웹 보안 설정을 사용자 정의하는 Bean
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/error");
    }

    // HTTP 보안 설정을 구성하는 SecurityFilterChain Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        // FORWARD 타입의 디스패처를 사용하는 요청은 모두 허용
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        // 특정 URL 패턴에 대한 요청은 모두 허용
                        .requestMatchers("/", "/login", "/signup", "/user", "/board/list", "/board/view/**", "/main", "/layout", "/img/**", "/css/**", "/js/**", "/username", "/files/**").permitAll()
                        // '/mypage/**' URL 패턴에 대한 요청은 인증된 사용자만 허용
                        .requestMatchers("/mypage/**").authenticated()
                        // '/admin/**' URL 패턴에 대한 요청은 ADMIN 역할을 가진 사용자만 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증된 사용자만 허용
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        // 로그인 페이지 URL 설정
                        .loginPage("/login")
                        // 로그인 시 사용할 사용자 이름 파라미터 설정
                        .usernameParameter("email")
                        // 로그인 성공 시 기본 리디렉션 URL 설정
                        .defaultSuccessUrl("/")
                )
                .logout(logout -> logout
                        // 로그아웃 성공 시 리디렉션 URL 설정
                        .logoutSuccessUrl("/")
                        // 세션 무효화 설정
                        .invalidateHttpSession(true)
                        // 'JSESSIONID' 쿠키 삭제 설정
                        .deleteCookies("JSESSIONID")
                )
                // CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2Login -> oauth2Login
                        // OAuth2 로그인 페이지 URL 설정
                        .loginPage("/login")
                        // OAuth2 로그인 성공 시 기본 리디렉션 URL 설정
                        .defaultSuccessUrl("/")
                        // 사용자 정보 가져올 서비스 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                )
                .build();
    }

    // SecurityContextRepository Bean 설정
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
