package com.example.demo.controller;

import com.example.demo.domain.Users;
import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/myPage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get the current logged-in user's email
        String email = userDetails.getUsername();
        // Fetch the member details using the email
        Users user = memberService.findByEmail(email);
        // Add the member details to the model to be displayed on the My Page
        model.addAttribute("user", user);
        return "myPage";
    }
}
