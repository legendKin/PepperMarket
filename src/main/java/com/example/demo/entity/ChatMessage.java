package com.example.demo.entity;

// 필요한 클래스와 어노테이션을 가져옴
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// 채팅 메시지를 나타내는 엔티티 클래스
@Getter
@Setter
@Entity // JPA 엔티티임을 나타내는 어노테이션
@Table(name = "chat_messages") // 해당 엔티티를 매핑할 테이블 이름을 지정하는 어노테이션
public class ChatMessage {

    @Id // 주요 키를 나타내는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성되는 값임을 나타내는 어노테이션
    private Long id; // 채팅 메시지의 고유 식별자

    @Column(nullable = false) // 데이터베이스 열의 매핑 정보를 나타내는 어노테이션
    private String userId; // 채팅 메시지를 보낸 사용자의 ID

    @Column(nullable = false) // 데이터베이스 열의 매핑 정보를 나타내는 어노테이션
    private String message; // 채팅 메시지 내용

    @Column(nullable = false) // 데이터베이스 열의 매핑 정보를 나타내는 어노테이션
    private LocalDateTime timestamp; // 채팅 메시지가 보내진 시각


}
