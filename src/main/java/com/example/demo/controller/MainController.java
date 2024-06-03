package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.PrincipalDetails;
import com.example.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller // Spring MVC에서 컨트롤러로 작동하도록 지정
public class MainController {

    @Autowired // BoardService를 주입받아 사용
    private BoardService boardService;

    // 루트 URL ("/") 요청을 처리하는 메서드
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            model.addAttribute("user", principalDetails.getUsers());
        }
        // 조회수가 높은 상위 10개의 게시글을 가져옴
        List<Board> topPosts = boardService.getTop10PostsByViewcount();
        // 모델에 topPosts라는 이름으로 게시글 리스트를 추가
        model.addAttribute("topPosts", topPosts);
        // "main" 뷰를 반환 (resources/templates/main.html 파일을 가리킴)
        return "main"; // main.html 템플릿 반환
    }

    // "/chat" URL 요청을 처리하는 메서드
    @GetMapping("/chat")
    public String chat() {
        // "chatter" 뷰를 반환 (resources/templates/chatter.html 파일을 가리킴)
        return "chatter";
    }
}
