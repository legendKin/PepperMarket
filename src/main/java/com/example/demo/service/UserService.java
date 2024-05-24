package com.example.demo.service;

import com.example.demo.domain.Users;
import com.example.demo.dto.AddUserRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 새로운 사용자를 저장하는 메서드
    public Long save(AddUserRequest dto) {
        // 사용자 정보를 생성하고 저장합니다.
        Users newUser = Users.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 비밀번호 암호화
                .nickname(dto.getNickname())  // 닉네임 추가
                .build();

        // 저장된 사용자의 ID를 반환합니다.
        return userRepository.save(newUser).getId();
    }
}
