package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Log4j2
public class ChatController {

    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable String receiverId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        String username = principalDetails.getUsername(); // 로그인된 사용자의 이름 가져오기
        model.addAttribute("username", username);
        model.addAttribute("receiverId", receiverId); // 상대방 사용자 ID 추가
        return "chater";
    }
}

