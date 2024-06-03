package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, UserRepository userRepository) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());

        Users sender = userRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        Users receiver = userRepository.findById(chatMessage.getReceiverId()).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);

        return chatMessageService.saveChatMessage(chatMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage) {
        chatMessage.setContent(chatMessage.getSender().getNickname() + " joined the chat");
        chatMessage.setTimestamp(LocalDateTime.now());

        Users sender = userRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        chatMessage.setSender(sender);

        return chatMessageService.saveChatMessage(chatMessage);
    }
}
