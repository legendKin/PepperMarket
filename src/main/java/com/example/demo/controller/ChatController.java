package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.PrincipalDetails;
import com.example.demo.service.ChatMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@Log4j2
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/previous-chat-messages/{userId}")
    public ResponseEntity<List<ChatMessage>> getPreviousChatMessages(@PathVariable String userId) {
        List<ChatMessage> previousMessages = chatMessageService.getPreviousChatMessages(userId);
        return ResponseEntity.ok(previousMessages);
    }

    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable String receiverId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String username = principalDetails.getName();
        model.addAttribute("username", username);
        model.addAttribute("receiverId", receiverId);
        return "chatter";
    }
}
