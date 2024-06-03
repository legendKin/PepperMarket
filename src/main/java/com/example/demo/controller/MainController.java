package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String index(Model model) {

        List<Board> topPosts = boardService.getTop10PostsByViewcount();
        model.addAttribute("topPosts", topPosts);

        return "main";
    }

    @GetMapping("/chat")
    public String chat() {
        return "chatter";
    }
}
