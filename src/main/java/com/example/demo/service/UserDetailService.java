package com.example.demo.service;

import com.example.demo.entity.PrincipalDetails;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security의 UserDetailsService를 구현하는 클래스입니다.
 * 사용자의 로그인 정보를 가져오는 역할을 합니다.
 */
@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository; // 사용자 레포지토리

    /**
     * 주어진 이메일을 사용하여 사용자의 정보를 가져오는 메서드입니다.
     * @param email 사용자 이메일
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생하는 예외
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.")); // 사용자를 찾지 못하면 예외 발생

        return new PrincipalDetails(user);
    }
}
