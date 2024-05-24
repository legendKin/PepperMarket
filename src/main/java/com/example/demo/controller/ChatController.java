package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import com.example.demo.entity.ChatMessage;
import com.example.demo.service.ChatMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Log4j2
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/previous-chat-messages")
    public ResponseEntity<List<ChatMessage>> getPreviousChatMessages() {
        // 데이터베이스에서 이전 채팅 메시지를 가져와서 클라이언트에게 응답
        List<ChatMessage> previousMessages = chatMessageService.getPreviousChatMessages();
        return ResponseEntity.ok(previousMessages);
    }

    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable String receiverId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String username = principalDetails.getUsername(); // 로그인된 사용자의 이름 가져오기
        model.addAttribute("username", username);
        model.addAttribute("receiverId", receiverId); // 상대방 사용자 ID 추가



        return "chater";
    }
}
