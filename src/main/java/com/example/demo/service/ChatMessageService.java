package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 채팅 메시지와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * 새로운 채팅 메시지를 저장하는 메서드입니다.
     * @param chatMessage 저장할 채팅 메시지
     */
    public void saveChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    /**
     * 이전 채팅 메시지 목록을 조회하는 메서드입니다.
     * @return 이전 채팅 메시지 목록
     */
    public List<ChatMessage> getPreviousChatMessages(String userId) {
        // 유저 ID로 채팅 메시지를 조회하고 타임스탬프 순서대로 정렬하여 반환
        return chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
    }
}
