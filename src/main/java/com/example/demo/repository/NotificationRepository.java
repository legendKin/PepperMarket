package com.example.demo.repository;

import com.example.demo.domain.Notification;
import com.example.demo.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(Users user);
}