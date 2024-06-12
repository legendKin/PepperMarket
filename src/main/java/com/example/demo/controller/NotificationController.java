package com.example.demo.controller;

import com.example.demo.entity.Notification;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // RESTful 웹 서비스를 위한 컨트롤러
@RequestMapping("/notifications") // "/notifications" 경로로 들어오는 요청을 처리
public class NotificationController {

    @Autowired // NotificationService를 주입받아 사용
    private NotificationService notificationService;

    @Autowired // UserRepository를 주입받아 사용
    private UserRepository userRepository;

    // 특정 사용자의 알림 리스트를 가져오는 엔드포인트
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam Long userId) {
        // 주어진 userId로 사용자 정보를 검색
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // 사용자의 알림 리스트를 가져와서 HTTP 응답으로 반환
        return ResponseEntity.ok(notificationService.findByUser(user));
    }

    // 특정 알림을 읽음 상태로 표시하는 엔드포인트
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        // 주어진 알림 ID로 알림을 읽음 상태로 표시
        notificationService.markAsRead(id);
        // HTTP 204 No Content 응답 반환
        return ResponseEntity.noContent().build();
    }
    
    
}
