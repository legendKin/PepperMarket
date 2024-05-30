package com.example.demo.service;

import com.example.demo.entity.Keyword;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private UserRepository userRepository;  // UserRepository 주입

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public List<Notification> findByUser(Users user) {
        return notificationRepository.findByUser(user);
    }

    public void notify(Board board) {
        // 모든 사용자를 조회
        List<Users> usersList = userRepository.findAll();

        for (Users user : usersList) {
            // 각 사용자의 키워드를 조회
            List<Keyword> userKeywords = keywordService.findByUser(user);
            System.out.println("Checking keywords for user ID: " + user.getId());

            // 게시글 제목이 사용자의 키워드에 포함되는지 확인
            for (Keyword keyword : userKeywords) {
                System.out.println("Checking keyword: " + keyword.getKeyword());
                if (board.getTitle().contains(keyword.getKeyword())) {
                    // 키워드가 일치할 경우에만 알림 생성
                    Notification notification = new Notification();
                    notification.setUser(user); // 알림을 받을 사용자 설정
                    notification.setBoard(board);
                    notification.setMessage("A new board matches your keyword: " + board.getTitle());
                    notification.setRead(false);
                    notificationRepository.save(notification);
                    System.out.println("Notification saved for user ID: " + user.getId() + " for board ID: " + board.getId());
                }
            }
        }
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
