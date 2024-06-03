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

@Controller // Spring MVC에서 컨트롤러로 작동하도록 지정
public class MyPageController {

    @Autowired // MemberService를 주입받아 사용
    private MemberService memberService;

    // "mypage" URL 요청을 처리하는 메서드
    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal UserDetails userDetails, Model model) throws Exception {
        // 로그인한 사용자의 이메일을 가져옴
        String email = userDetails.getUsername();
        // 이메일을 통해 사용자 정보를 가져옴
        Users user = memberService.findByEmail(email);
        // 모델에 사용자 정보를 추가
        model.addAttribute("user", user);
        // "mypage" 뷰를 반환 (resources/templates/mypage.html 파일을 가리킴)
        return "mypage";
    }

    // 프로필 사진 변경 요청을 처리하는 메서드
    @PostMapping("/mypage/change-profile-picture")
    public String changeProfilePicture(@RequestParam("file") MultipartFile file,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        // 파일이 비어있는지 확인
        if (file.isEmpty()) {
            // 파일이 없으면 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/mypage";
        }

        try {
            // 파일을 저장하고 파일 이름을 반환받음
            String fileName = memberService.storeFile(file);
            // 사용자의 프로필 사진을 업데이트
            memberService.updateUserProfilePicture(userDetails.getUsername(), fileName);
            // 성공 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + fileName + "'");
        } catch (IOException e) {
            // 파일 업로드 실패 시 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            // 일반 오류 발생 시 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
        }

        return "redirect:/mypage";
    }

    // 프로필 정보 변경 요청을 처리하는 메서드
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
            // 사용자 프로필 정보를 업데이트
            memberService.updateUserProfileInfo(userDetails.getUsername(), file, nickname, email, name, age, birthdate);
            // 성공 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully.");
        } catch (IOException e) {
            // 파일 업로드 실패 시 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            // 일반 오류 발생 시 메시지 추가 후 리다이렉트
            redirectAttributes.addFlashAttribute("message", "An error occurred: " + e.getMessage());
        }

        return "redirect:/mypage";
    }
}
