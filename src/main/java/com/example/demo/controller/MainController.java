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
        String profilepic = "";
        try{
            nickname = principalDetails.getName();
        }
        catch (Exception e){
            nickname = "anon";
        }finally{
            model.addAttribute("nickname", nickname);
        }

        try{
            profilepic = principalDetails.getPPic();
        }
        catch (Exception e){
            profilepic ="https://cdn-icons-png.flaticon.com/512/3135/3135715.png";
        }finally{
            model.addAttribute("profilepic", profilepic);
        }

        return "main";
    }
    @GetMapping("/chat")
    public String chat(){
        return "chatter";
    }


}
