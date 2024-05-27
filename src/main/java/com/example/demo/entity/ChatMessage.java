package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 채팅 메시지를 나타내는 엔티티 클래스입니다.
 */
@Getter
@Setter
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 메시지 식별자

    @Column(nullable = false)
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private String message; // 메시지 내용

    @Column(nullable = false)
    private LocalDateTime timestamp; // 타임스탬프


}
