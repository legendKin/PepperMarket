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
@Service
public class MemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 사용자 이메일로 사용자 찾기
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
        return file.getOriginalFilename();
    }

    // 프로필 사진 업데이트
    public void updateUserProfilePicture(String username, String fileName) throws Exception {
        Users user = findByEmail(username);
        user.setProfilePictureUrl(fileName);
        userRepository.save(user);
    }

    // 프로필 정보 업데이트
    public void updateUserProfileInfo(String username, MultipartFile file, String nickname, String email, String name, Date birthdate) throws Exception {
        Users user = findByEmail(username);
        if (!file.isEmpty()) {
            String fileName = storeFile(file);
            user.setProfilePictureUrl(("/files/" + fileName));
        }
        user.setNickname(nickname);
        user.setEmail(email);
        user.setName(name);
        userRepository.save(user);
        user.setBirthdate(birthdate);
    }

}
