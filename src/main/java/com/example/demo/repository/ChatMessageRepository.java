package com.example.demo.repository;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 사용자 ID를 통해 참여한 채팅방 ID를 조회하는 메서드
    @Query("SELECT DISTINCT cm.chatRoomId FROM ChatMessage cm WHERE cm.sender.email = :userEmail OR cm.receiver.email = :userEmail")
    List<String> findChatRoomsByUserEmail(@Param("userEmail") String userEmail);

    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sender = :sender OR cm.receiver = :receiver")
    List<ChatMessage> findBySenderOrReceiver(@Param("sender") Users sender, @Param("receiver") Users receiver);

    List<ChatMessage> findByReceiver(Users receiver);
    List<ChatMessage> findBySender(Users sender);
}
