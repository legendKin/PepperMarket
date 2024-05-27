package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 게시글 댓글과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository; // 댓글 레포지토리

    @Autowired
    private BoardRepository boardRepository; // 게시글 레포지토리

    /**
     * 특정 게시글에 대한 댓글 목록을 조회하는 메서드입니다.
     * @param boardId 조회할 게시글의 ID
     * @return 특정 게시글에 대한 댓글 목록
     */
    public List<Comment> getCommentsByBoardId(Integer boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    /**
     * 특정 게시글에 댓글을 추가하는 메서드입니다.
     * @param boardId 댓글을 추가할 게시글의 ID
     * @param content 댓글 내용
     * @param author 댓글 작성자
     */
    public void addComment(Integer boardId, String content, String author) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Invalid board ID: " + boardId));
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setContent(content);
        comment.setAuthor(author);

        commentRepository.save(comment);
    }

    /**
     * 특정 게시글에 대한 모든 댓글을 삭제하는 메서드입니다.
     * @param boardId 삭제할 게시글의 ID
     */
    @Transactional
    public void deleteCommentsByBoardId(Integer boardId) {
        commentRepository.deleteByBoardId(boardId);
    }

}
