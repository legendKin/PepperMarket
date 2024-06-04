package com.example.demo.service;

import com.example.demo.entity.ChatRoom;
import com.example.demo.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom createChatRoom(Long senderId, Long receiverId) {
        ChatRoom chatRoom = new ChatRoom(senderId, receiverId);
        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> findByUserId(Long userId) {
        return chatRoomRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}
