package com.example.demo.repository;

import com.example.demo.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 특정 사용자의 채팅 메시지를 타임스탬프 오름차순으로 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 타임스탬프 오름차순으로 정렬된 채팅 메시지 목록
     */
    List<ChatMessage> findByUserIdOrderByTimestampAsc(String userId);

    /**
     * 특정 사용자의 채팅 메시지를 페이징 처리하여 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징된 채팅 메시지 목록
     */
    List<ChatMessage> findByUserId(String userId, Pageable pageable);
}
