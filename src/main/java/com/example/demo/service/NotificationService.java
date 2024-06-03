package com.example.demo.service;

import com.example.demo.entity.Keyword;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

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
        List<Users> usersList = userRepository.findAll();

        for (Users user : usersList) {
            List<Keyword> userKeywords = keywordService.findByUser(user);
            logger.info("Checking keywords for user ID: " + user.getId());

            for (Keyword keyword : userKeywords) {
                logger.info("Checking keyword: " + keyword.getKeyword());
                if (board.getTitle().contains(keyword.getKeyword())) {
                    Notification notification = new Notification();
                    notification.setUser(user);
                    notification.setBoard(board);
                    notification.setMessage("A new board matches your keyword: " + board.getTitle());
                    notification.setRead(false);
                    notificationRepository.save(notification);
                    logger.info("Notification saved for user ID: " + user.getId() + " for board ID: " + board.getId());
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
