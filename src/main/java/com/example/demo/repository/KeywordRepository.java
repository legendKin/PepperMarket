package com.example.demo.repository;

import com.example.demo.domain.Keyword;
import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByUser(Users user);
}
