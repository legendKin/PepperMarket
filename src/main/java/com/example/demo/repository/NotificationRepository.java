package com.example.demo.repository;

import com.example.demo.domain.Notification;
import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(Users user);
    @Transactional
    void deleteByBoardId(Long boardId);
}