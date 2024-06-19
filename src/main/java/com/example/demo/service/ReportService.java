package com.example.demo.service;

import com.example.demo.entity.Report;
import com.example.demo.entity.Users;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    public void createReport(String reporterEmail, Long reportedUserId, String reason) {
        Users reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Users reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter email"));

        Report report = new Report();
        report.setReporter(reporterEmail);
        report.setReportedUser(reportedUser);
        report.setReason(reason);
        report.setReportedAt(new Date());
        report.setReporterEmail(reporterEmail);
        report.setReportedEmail(reportedUser.getEmail());
        reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }
}
