package com.example.demo.controller;

import com.example.demo.entity.PrincipalDetails;
import com.example.demo.service.BoardService;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;


// 프로젝트 전역적으로 적용되는 컨트롤러입니다.


@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BoardService boardService;


    @ModelAttribute
    public void addAttributes(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String nick = "";
        String profilepic = "";
        Long loggedUserId = 0L;
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

        try{
            loggedUserId = principalDetails.getId();
        } catch(Exception e){
            loggedUserId = Long.valueOf("0");
        }finally {
            model.addAttribute("loggedUserId", loggedUserId);

        }

        List<String> categList = categoryService.getCategoryList();
        model.addAttribute("categList", categList);



    }


}