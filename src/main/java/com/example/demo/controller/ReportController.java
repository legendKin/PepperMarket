package com.example.demo.controller;

import com.example.demo.entity.Report;
import com.example.demo.entity.Users;
import com.example.demo.service.ReportService;
import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/reports")
    public String getAllReports(@AuthenticationPrincipal UserDetails userDetails, Model model) throws Exception {
        String email = userDetails.getUsername();
        Users user = memberService.findByEmail(email);
        if (user.getRole().equals("admin")) {
            List<Report> reports = reportService.getAllReports();
            model.addAttribute("reports", reports);
            return "reports"; // reports.html 뷰를 반환합니다.
        } else {
            return "redirect:/"; // 관리자가 아닌 경우 메인 페이지로 리디렉션합니다.
        }
    }

    @PostMapping("/report")
    public String createReport(@RequestParam("reporter") String reporter,
                               @RequestParam("reportedUser") String reportedUser,
                               @RequestParam("reason") String reason) {
        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(reason)
                .reportedAt(new java.util.Date())
                .build();
        reportService.createReport(report);
        return "redirect:/reports"; // 신고 목록 페이지로 리디렉션합니다.
    }
}
