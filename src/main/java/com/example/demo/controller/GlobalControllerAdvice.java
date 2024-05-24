package com.example.demo.controller;

import com.example.demo.domain.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


// 프로젝트 전역적으로 적용되는 컨트롤러입니다.


@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addAttributes(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String nick = "";
        String profilepic = "";
        try {
            nick = principalDetails.getName();
        } catch (Exception e) {
            nick = "anon";
        } finally {
            model.addAttribute("nickname", nick);
        }

        try {
            profilepic = principalDetails.getPPic();
        } catch (Exception e) {
            profilepic = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";
        } finally {
            model.addAttribute("profilepic", profilepic);
        }
    }

}