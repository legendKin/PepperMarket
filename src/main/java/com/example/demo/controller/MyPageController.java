package com.example.demo.controller;

import com.example.demo.entity.Users;
import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;

@Controller
public class MyPageController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) throws Exception {
        String email = userDetails.getUsername();
        Users user = memberService.findByEmail(email);
        model.addAttribute("user", user);
        return "mypage";
    }

    @PostMapping("/mypage/change-profile-picture")
    public String changeProfilePicture(@RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/mypage";
        }

        try {
            String fileName = memberService.storeFile(file);
            memberService.updateUserProfilePicture(userDetails.getUsername(), fileName);
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + fileName + "'");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
        }

        return "redirect:/mypage";
    }

    @PostMapping("/mypage/change-profile-info")
    public String changeProfileInfo(@RequestParam("file") MultipartFile file,
                                    @RequestParam("nickname") String nickname,
                                    @RequestParam("email") String email,
                                    @RequestParam("name") String name,
                                    @RequestParam("age") Integer age,
                                    @RequestParam("birthdate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthdate,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            memberService.updateUserProfileInfo(userDetails.getUsername(), file, nickname, email, name, age, birthdate);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
        }

        return "redirect:/mypage";
    }


}
