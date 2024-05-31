package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        // sender와 receiver를 Users 엔티티로 설정
        Users sender = userRepository.findById(chatMessage.getSender().getId()).orElseThrow();
        Users receiver = userRepository.findById(chatMessage.getReceiver().getId()).orElseThrow();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessagesByChatRoomId(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

    public List<ChatMessage> getMessagesByReceiver(Users receiver) {
        return chatMessageRepository.findByReceiver(receiver);
    }

    public List<ChatMessage> getMessagesBySenderOrReceiver(Users sender, Users receiver) {
        return chatMessageRepository.findBySenderOrReceiver(sender, receiver);
    }
}
