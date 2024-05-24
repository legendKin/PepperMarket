package com.example.demo.repository;


import com.example.demo.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 게시판 관련 데이터베이스 작업을 수행하는 레포지토리 인터페이스
@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    // 제목을 포함하는 게시글을 페이징하여 조회하는 메서드
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}
