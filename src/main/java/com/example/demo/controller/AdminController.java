package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
// 여기에 admin 계정만 보이는 페이지 추가
    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // admin.html 페이지를 반환
    }
}
