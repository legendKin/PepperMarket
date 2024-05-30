package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.dto.AddUserRequest;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 사용자 관련 기능을 처리하는 서비스 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository; // 사용자 레포지토리
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화를 위한 인코더

    /**
     * 새로운 사용자를 등록하고 사용자의 ID를 반환합니다.
     * @param dto 새로 등록할 사용자의 정보를 담은 DTO 객체
     * @return 새로 등록된 사용자의 ID
     */
    public Long save(AddUserRequest dto) {
        // 사용자 정보를 생성하고 저장
        Users newUser = Users.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 비밀번호를 암호화하여 저장
                .nickname(dto.getNickname()) // 닉네임 추가
                .build();
        return userRepository.save(newUser).getId(); // 사용자의 ID 반환
    }



    public Optional<Users> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}