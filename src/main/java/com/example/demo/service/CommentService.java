package com.example.demo.service;


import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

// 댓글 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository; // 댓글 레포지토리 주입

    @Autowired
    private BoardRepository boardRepository; // 게시글 레포지토리 주입

    // 특정 게시글의 댓글 가져오기
    public List<Comment> getCommentsByBoardId(Integer boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    // 특정 게시글에 댓글 추가
    public void addComment(Integer boardId, String content, String author) {
        // 주어진 게시글 ID로 게시글을 찾음
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board ID: " + boardId));
        // 새로운 댓글 생성
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent(content);
        comment.setAuthor(author);

        commentRepository.save(comment); // 댓글 저장
    }

    // 특정 게시글의 댓글 삭제
    @Transactional
    public void deleteCommentsByBoardId(Integer boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
