package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글을 나타내는 엔티티 클래스입니다.
 */
@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 댓글 식별자

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board; // 댓글이 속한 게시글

    private String content; // 댓글 내용

    private String author; // 사용자의 별명 또는 닉네임을 저장하는 필드
}
