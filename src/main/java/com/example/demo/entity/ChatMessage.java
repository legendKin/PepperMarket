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

    // 컬럼 이름을 상수로 정의
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id; // 메시지 식별자

    @Column(name = USER_ID, nullable = false)
    private String userId; // 사용자 ID

    @Column(name = MESSAGE, nullable = false)
    private String message; // 메시지 내용

    @Column(name = TIMESTAMP, nullable = false)
    private LocalDateTime timestamp; // 타임스탬프

    /**
     * 기본 생성자.
     */
    public ChatMessage() {
    }

    /**
     * 모든 필드를 초기화하는 생성자.
     *
     * @param userId    사용자 ID
     * @param message   메시지 내용
     * @param timestamp 타임스탬프
     */
    public ChatMessage(String userId, String message, LocalDateTime timestamp) {
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * ID를 제외한 모든 필드를 초기화하는 생성자.
     * @param userId 사용자 ID
     * @param message 메시지 내용
     * @param timestamp 타임스탬프
     */
    public ChatMessage(Long id, String userId, String message, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }
}
