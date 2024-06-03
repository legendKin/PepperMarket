package com.example.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(Model model) {
        // 오류 페이지로 이동
        model.addAttribute("message", "오류가 발생했습니다.");
        return "error";  // 오류 페이지로 이동
    }

    public String getErrorPath() {
        return "/error";
    }
}

