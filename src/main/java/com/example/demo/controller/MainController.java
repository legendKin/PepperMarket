package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String nickname = "";
        try{
            nickname = principalDetails.getName();
        }
        catch (Exception e){
            nickname = "anon";
        }finally{
            model.addAttribute("nickname", nickname);
        }
        return "main";
    }
    @GetMapping("/chat")
    public String chat(){
        return "chatter";
    }

}
