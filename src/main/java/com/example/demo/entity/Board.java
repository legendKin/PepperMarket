package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 게시글을 나타내는 엔티티 클래스입니다.
 */
@Getter
@Setter
@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 게시글 식별자

    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String filename; // 파일 이름
    private String filepath; // 파일 경로
    private Integer viewcount; // 조회수

    @OneToMany(mappedBy = "board")
    private List<Comment> comments; // 게시글에 달린 댓글 목록

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user; // 게시글 작성자

    @Column(name = "createdate", nullable = true)
    private LocalDateTime createDate; // 생성일

    @Column(name = "modifydate", nullable = true)
    private LocalDateTime modifyDate; // 수정일
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // createDate를 기준으로 몇 분 전, 몇 시간 전 등을 반환하는 메서드
    public String getTimeAgo() {
        if (this.createDate == null) {
            return "날짜 정보 없음";
        }
        Date date = Date.from(this.createDate.atZone(ZoneId.systemDefault()).toInstant());
        return Time.calculateTime(date);
    }
}

