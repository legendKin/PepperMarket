package com.example.demo.entity;

// 필요한 클래스와 어노테이션을 가져옴
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 댓글을 나타내는 엔티티 클래스
@Entity // JPA 엔티티임을 나타내는 어노테이션
@Getter // lombok을 사용하여 getter 메서드 자동 생성
@Setter // lombok을 사용하여 setter 메서드 자동 생성
public class Comment {
    @Id // 주요 키를 나타내는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 값임을 나타내는 어노테이션
    private Integer id; // 댓글의 고유 식별자

    @ManyToOne // 다대일 관계를 나타내는 어노테이션
    @JoinColumn(name = "board_id") // 외래 키를 나타내는 어노테이션
    private Board board; // 댓글이 속한 게시글 정보

    private String content; // 댓글 내용

    // 사용자의 별명 또는 닉네임을 저장하는 필드
    private String author;

}
