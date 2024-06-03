package com.example.demo.service;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<ChatMessage> getChatRoomsAndLastMessagesByUser(Users user) {
        List<ChatMessage> sentMessages = chatMessageRepository.findBySender(user);
        List<ChatMessage> receivedMessages = chatMessageRepository.findByReceiver(user);

        Map<String, ChatMessage> chatRooms = new HashMap<>();
        for (ChatMessage message : sentMessages) {
            chatRooms.putIfAbsent(message.getChatRoomId(), message);
            if (message.getTimestamp().isAfter(chatRooms.get(message.getChatRoomId()).getTimestamp())) {
                chatRooms.put(message.getChatRoomId(), message);
            }
        }
        for (ChatMessage message : receivedMessages) {
            chatRooms.putIfAbsent(message.getChatRoomId(), message);
            if (message.getTimestamp().isAfter(chatRooms.get(message.getChatRoomId()).getTimestamp())) {
                chatRooms.put(message.getChatRoomId(), message);
            }
        }
        return new ArrayList<>(chatRooms.values());
    }
}
