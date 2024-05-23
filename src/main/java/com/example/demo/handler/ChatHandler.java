package com.example.demo.handler;

import com.example.demo.entity.ChatMessage;
import com.example.demo.service.ChatMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class ChatHandler extends TextWebSocketHandler {
    private final ChatMessageService chatMessageService;
    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    @Autowired
    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : " + payload);
        String[] parts = payload.split(":", 2); // 최대 2개의 부분으로 분할
        if (parts.length < 2) {
            log.warn("Invalid message format: " + payload);
            return; // 유효하지 않은 메시지 형식이면 처리하지 않음
        }
        String senderId = parts[0];
        String msg = parts[1];

        // 채팅 메시지 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(senderId); // session.getUri().getQuery().split("=")[1] 대신 senderId 사용
        chatMessage.setMessage(msg);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageService.saveChatMessage(chatMessage);

        for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
            if (!entry.getKey().equals(senderId)) {
                entry.getValue().sendMessage(new TextMessage(senderId + ": " + msg));
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = session.getUri().getQuery().split("=")[1];
        sessionMap.put(username, session);
        log.info(username + " 클라이언트 접속");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = session.getUri().getQuery().split("=")[1];
        log.info(username + " 클라이언트 접속 해제");
        sessionMap.remove(username);
    }
}
