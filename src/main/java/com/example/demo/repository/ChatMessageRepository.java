package com.example.demo.repository;


import com.example.demo.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 채팅 메시지 관련 데이터베이스 작업을 수행하는 레포지토리 인터페이스
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 사용자 ID를 기준으로 오름차순으로 정렬된 채팅 메시지 목록을 가져오는 메서드
    List<ChatMessage> findByUserIdOrderByTimestampAsc(String userId);

    // 사용자 ID를 기준으로 채팅 메시지 목록을 가져오는 메서드
    List<ChatMessage> findByUserId(String userId);
}
