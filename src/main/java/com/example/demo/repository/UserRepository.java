package com.example.demo.repository;

import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // email로 사용자 정보 가져옴
    Optional<Users> findByEmail(String email);

    Optional<Users> findByNickname(String nickname); // nickname으로 사용자 정보 가져옴
}
