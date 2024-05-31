package com.example.demo.repository;


import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // email로 사용자 정보 가져옴 (findByEmail을 사용했기 때문) :이부분에 id를 사용하면 id로 사용자 정보 가졍
    Optional<Users> findByEmail(String email);


}