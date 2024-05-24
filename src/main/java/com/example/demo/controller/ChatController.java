package com.example.demo.controller;

// 필요한 클래스와 어노테이션을 가져옴
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

// 이 클래스가 스프링 컨트롤러임을 나타내고 로그4j를 사용하여 로깅을 지원
@Controller
@Log4j2
public class ChatController {

    private final ChatMessageService chatMessageService; // ChatMessageService 주입을 위한 필드 선언

    // 생성자 주입을 통한 ChatMessageService 주입
    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 이전 채팅 메시지 목록을 가져와서 클라이언트에게 응답
    @GetMapping("/previous-chat-messages")
    public ResponseEntity<List<ChatMessage>> getPreviousChatMessages() {
        // 데이터베이스에서 이전 채팅 메시지를 가져와서 리스트로 반환
        List<ChatMessage> previousMessages = chatMessageService.getPreviousChatMessages();
        return ResponseEntity.ok(previousMessages); // HTTP 200 OK 응답으로 채팅 메시지 리스트 반환
    }

    // 특정 사용자와의 채팅 화면을 표시
    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable String receiverId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String username = principalDetails.getUsername(); // 로그인된 사용자의 이름 가져오기
        model.addAttribute("username", username); // 모델에 사용자 이름 추가
        model.addAttribute("receiverId", receiverId); // 모델에 상대방 사용자 ID 추가

        return "chater"; // 채팅 화면 뷰 이름 반환
    }
}
