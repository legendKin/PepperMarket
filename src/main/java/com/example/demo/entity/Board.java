package com.example.demo.entity;

// 필요한 클래스와 어노테이션을 가져옴
import java.time.LocalDateTime;
import com.example.demo.domain.Users;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// 게시판을 나타내는 엔티티 클래스
@Getter // lombok을 사용하여 getter 메서드 자동 생성
@Setter // lombok을 사용하여 setter 메서드 자동 생성
@Entity // JPA 엔티티임을 나타내는 어노테이션
@Data // lombok을 사용하여 equals(), hashCode(), toString(), getter, setter 메서드 등을 자동 생성
public class Board {
    @Id // 주요 키를 나타내는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 값임을 나타내는 어노테이션
    private Integer id; // 게시글의 고유 식별자

    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private String filename; // 첨부 파일의 이름
    private String filepath; // 첨부 파일의 경로
    private Integer viewcount; // 조회수

    @ManyToOne // 다대일 관계를 나타내는 어노테이션
    @JoinColumn(name = "user_id") // 외래 키를 나타내는 어노테이션
    private Users user; // 게시글 작성자 정보

    @Column(name = "createdate", nullable = true) // 데이터베이스 열의 매핑 정보를 나타내는 어노테이션
    private LocalDateTime createDate; // 게시글 생성일

    @Column(name = "modifydate", nullable = true) // 데이터베이스 열의 매핑 정보를 나타내는 어노테이션
    private LocalDateTime modifyDate; // 게시글 수정일
}
