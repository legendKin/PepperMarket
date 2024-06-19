package com.example.demo.controller;

import com.example.demo.dto.PasswordChangeRequest;
import com.example.demo.entity.Report;
import com.example.demo.entity.Users;
import com.example.demo.service.MemberService;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReportService reportService;

   
    @PostMapping("/change-nickname")
    public String changeNickname(@RequestParam("nickname") String nickname,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            memberService.updateNickname(userDetails.getUsername(), nickname);
            redirectAttributes.addFlashAttribute("message", "닉네임이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/mypage";
    }
    @PostMapping("/change-name")
    public String changeName(@RequestParam("name") String name,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            memberService.updateName(userDetails.getUsername(), name);
            redirectAttributes.addFlashAttribute("message", "이름이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/mypage";
    }
    @PostMapping("/change-birthdate")
    public String changeBirthdate(@RequestParam("birthdate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthdate,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            memberService.updateBirthdate(userDetails.getUsername(), birthdate);
            redirectAttributes.addFlashAttribute("message", "생년월일이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/mypage";
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            memberService.changePassword(userDetails.getUsername(), request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping("/is-social-login")
    public ResponseEntity<Boolean> isSocialLogin(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Users user = memberService.findByEmail(userDetails.getUsername());
            boolean isSocialLogin = user.getProvider() != null;
            return ResponseEntity.ok(isSocialLogin);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    
    
    
    
    
    @GetMapping("/reports")
    public String myReports(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        try {
            Users user = memberService.findByEmail(email);
            List<Report> myReports = reportService.getReportsByReporter(user.getEmail());
            model.addAttribute("myReports", myReports);
            return "mypage/reports"; // mypage/reports.html 뷰를 반환합니다.
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while fetching the data.");
            return "redirect:/error"; // 에러 발생 시 에러 페이지로 리디렉션합니다.
        }
    }

    @PostMapping("/report")
    public String createReport(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam("reportedUser") String reportedUser,
                               @RequestParam("reason") String reason,
                               RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();
        try {
            Users user = memberService.findByEmail(email);
            Report report = Report.builder()
                    .reporter(user.getEmail())
                    .reportedUser(reportedUser)
                    .reason(reason)
                    .reportedAt(new Date())
                    .build();
            reportService.createReport(report);
            redirectAttributes.addFlashAttribute("message", "Report created successfully.");
            return "redirect:/mypage/reports"; // 신고 목록 페이지로 리디렉션합니다.
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating the report.");
            return "redirect:/error"; // 에러 발생 시 에러 페이지로 리디렉션합니다.
        }
    }
}
