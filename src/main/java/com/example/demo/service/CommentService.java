package com.example.demo.service;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Users;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> getCommentsByBoardId(Integer boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    public void addComment(Integer boardId, String content, @AuthenticationPrincipal UserDetails userDetails) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Invalid board Id"));
        Users user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setNickname(user.getNickname());

        commentRepository.save(comment);
    }

    public void deleteCommentsByBoardId(Integer boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
