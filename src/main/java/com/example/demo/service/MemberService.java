package com.example.demo.service;

import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service // 서비스 클래스임을 나타냄
public class MemberService {

    private final UserRepository userRepository; // 사용자 관련 데이터 처리를 위한 리포지토리
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 인코더

    @Autowired // 생성자 주입
    public MemberService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 이메일을 통해 사용자를 조회하는 메서드
    public Users findByEmail(String email) throws Exception {
        Optional<Users> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new Exception("User not found");
        }
    }

    // 파일 저장 로직
    public String storeFile(MultipartFile file) throws IOException {
        // 파일 저장 로직 구현
        return file.getOriginalFilename(); // 예제: 파일의 원본 이름을 반환
    }

    // 사용자의 프로필 사진을 업데이트하는 메서드
    public void updateUserProfilePicture(String username, String fileName) throws Exception {
        Users user = findByEmail(username); // 이메일을 통해 사용자 조회
        user.setProfilePictureUrl(fileName); // 사용자 프로필 사진 URL 설정
        userRepository.save(user); // 사용자 정보 저장
    }

    // 사용자의 프로필 정보를 업데이트하는 메서드
    public void updateUserProfileInfo(String username, MultipartFile file, String nickname, String email, String name, Integer age, Date birthdate) throws IOException, Exception {
        Users user = findByEmail(username); // 이메일을 통해 사용자 조회
        if (!file.isEmpty()) { // 파일이 비어있지 않으면 파일 저장 및 프로필 사진 URL 설정
            String fileName = storeFile(file);
            user.setProfilePictureUrl(fileName);
        }
        user.setNickname(nickname); // 사용자 닉네임 설정
        user.setEmail(email); // 사용자 이메일 설정
        user.setName(name); // 사용자 이름 설정
        user.setAge(age); // 사용자 나이 설정
        user.setBirthdate(birthdate); // 사용자 생일 설정
        userRepository.save(user); // 사용자 정보 저장
    }
}
