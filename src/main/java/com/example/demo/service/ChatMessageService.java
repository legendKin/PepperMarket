package com.example.demo.service;


import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

// 채팅 메시지 관련 비즈니스 로직을 처리하는 서비스 클래스
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository; // 채팅 메시지 레포지토리 주입

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    // 채팅 메시지 저장
    public void saveChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    // 이전 채팅 메시지 가져오기
    public List<ChatMessage> getPreviousChatMessages() {
        // ChatMessageRepository에서 이전 채팅 메시지를 조회하는 메소드를 호출하여 반환
        return chatMessageRepository.findAll(); // findAll() 메소드 사용 예시. 실제로는 적절한 메소드를 사용해야 함
    }
}
