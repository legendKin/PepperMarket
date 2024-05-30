package com.example.demo.service;

import com.example.demo.domain.Users;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private UserRepository userRepository;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public MemberService() throws IOException {
        Files.createDirectories(this.fileStorageLocation);
    }

    public Users findByEmail(String email) {
        Optional<Users> user = userRepository.findByEmail(email);
        return user.orElse(null); // Optional에서 Users 객체를 추출하거나, null을 반환
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public void updateUserProfilePicture(String email, String fileName) {
        Optional<Users> user = userRepository.findByEmail(email);
        user.ifPresent(u -> {
            u.setProfilePictureUrl(fileName);
            userRepository.save(u);
        });
    }

    public void updateUserProfileInfo(String currentEmail, MultipartFile file, String nickname, String email) throws IOException {
        Optional<Users> userOptional = userRepository.findByEmail(currentEmail);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (!file.isEmpty()) {
                String fileName = storeFile(file);
                user.setProfilePictureUrl(fileName);
            }
            user.setNickname(nickname);
            user.setEmail(email);
            userRepository.save(user);
        }
    }
}
