package com.example.demo.repository;

import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}

