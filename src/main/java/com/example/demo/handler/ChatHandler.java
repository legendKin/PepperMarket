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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅 기능을 처리하는 웹소켓 핸들러 클래스입니다.
 */
@Component
@Log4j2
public class ChatHandler extends TextWebSocketHandler {

    private final ChatMessageService chatMessageService;
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    // ChatHandler 생성자
    @Autowired
    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // 클라이언트로부터 메시지를 받았을 때 실행되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received payload: {}", payload);
        String[] parts = payload.split(":", 2); // 최대 2개의 부분으로 분할
        if (parts.length < 2) {
            log.warn("Invalid message format: {}", payload);
            return; // 유효하지 않은 메시지 형식이면 처리하지 않음
        }
        String senderId = parts[0];
        String msg = parts[1];

        // 채팅 메시지 저장
        saveChatMessage(senderId, msg);

        // 세션맵에 있는 모든 클라이언트에게 메시지 전송
        broadcastMessage(senderId, msg);
    }

    private void saveChatMessage(String senderId, String msg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(senderId);
        chatMessage.setMessage(msg);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageService.saveChatMessage(chatMessage);
    }

    private void broadcastMessage(String senderId, String msg) throws Exception {
        for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
            if (!entry.getKey().equals(senderId)) {
                entry.getValue().sendMessage(new TextMessage(senderId + ": " + msg));
            }
        }
    }

    // 클라이언트가 연결되었을 때 실행되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsernameFromSession(session);
        sessionMap.put(username, session);
        log.info("{} 클라이언트 접속", username);
    }

    // 클라이언트가 연결을 종료했을 때 실행되는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsernameFromSession(session);
        sessionMap.remove(username);
        log.info("{} 클라이언트 접속 해제", username);
    }

    private String getUsernameFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null || !query.contains("=")) {
            log.warn("Invalid session query: {}", query);
            throw new IllegalArgumentException("Invalid session query");
        }
        return query.split("=")[1];
    }
}
