package com.example.demo.repository;


import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 댓글 관련 데이터베이스 작업을 수행하는 레포지토리 인터페이스
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 특정 게시글에 대한 댓글 목록을 가져오는 메서드
    List<Comment> findByBoardId(Integer boardId);

    // 특정 게시글에 대한 댓글을 삭제하는 메서드
    void deleteByBoardId(Integer boardId);
}
