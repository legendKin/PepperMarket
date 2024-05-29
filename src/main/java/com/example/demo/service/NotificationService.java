package com.example.demo.service;

import com.example.demo.domain.Notification;
import com.example.demo.domain.Users;
import com.example.demo.entity.Board;
import com.example.demo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    public List<Notification> findByUser(Users user) {
        return notificationRepository.findByUser(user);
    }

    public void notify(Users user, Board board) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setBoard(board);
        notification.setMessage("A new board matches your keyword: " + board.getTitle());
        notification.setRead(false);
        notificationRepository.save(notification);
        System.out.println("Notification saved for user ID: " + user.getId() + " for board ID: " + board.getId());
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
