package com.example.demo.service;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.domain.Users;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(email)

                .orElseThrow(()-> {  //원하는 객체(email)을 얻지 못하면 예외 처리
                    return new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."); //예외처리 메시지
                });

        return new PrincipalDetails(user);
    }

}