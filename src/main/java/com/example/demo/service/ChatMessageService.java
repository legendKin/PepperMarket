package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public void saveChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getPreviousChatMessages() {
        // ChatMessageRepository에서 이전 채팅 메시지를 조회하는 메소드를 호출하여 반환
        return chatMessageRepository.findAll(); // 예시로 findAll() 메소드 사용. 실제로는 적절한 메소드를 사용해야 함
    }
}
