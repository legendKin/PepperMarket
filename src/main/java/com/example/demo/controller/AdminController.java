package com.example.demo.controller;

import com.example.demo.entity.Report;
import com.example.demo.entity.Users;
import com.example.demo.service.MemberService;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/admin/reports")
    public String adminReports(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        try {
            Users user = memberService.findByEmail(email);
            if (user.getRole().equals("ROLE_ADMIN")) {
                List<Report> reports = reportService.getAllReports();
                model.addAttribute("reports", reports);
                return "admin/reports"; // admin/reports.html 뷰를 반환합니다.
            } else {
                return "redirect:/"; // 관리자가 아닌 경우 메인 페이지로 리디렉션합니다.
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while fetching the data.");
            return "error"; // 에러 페이지로 리디렉션합니다.
        }
    }
}
