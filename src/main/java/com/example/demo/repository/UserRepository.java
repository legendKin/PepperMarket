package com.example.demo.repository;


import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 사용자 관련 데이터베이스 작업을 수행하는 레포지토리 인터페이스
public interface UserRepository extends JpaRepository<Users, Long> {
    // 이메일로 사용자 정보를 가져오는 메서드
    Optional<Users> findByEmail(String email);
}
