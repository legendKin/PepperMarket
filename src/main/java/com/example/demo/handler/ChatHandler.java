package com.example.demo.handler;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @Autowired
    public ChatHandler(ChatMessageService chatMessageService, UserRepository userRepository) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 성립되면 세션을 맵에 저장
        String sessionId = session.getId();
        sessionMap.put(sessionId, session);
        log.info("세션 연결: {}", sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메세지 처리
        String payload = message.getPayload();
        log.info("수신한 메세지: {}", payload);

        // 예제: payload를 ChatMessage로 변환
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(payload);
        chatMessage.setTimestamp(LocalDateTime.now());

        // sender와 receiver 설정 (예제 ID 사용, 실제 사용 시 동적 할당 필요)
        Users sender = userRepository.findById(1L).orElseThrow();
        Users receiver = userRepository.findById(2L).orElseThrow();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);

        // 메세지 저장
        chatMessageService.saveChatMessage(chatMessage);

        // 모든 세션에 메세지 전송
        for (WebSocketSession webSocketSession : sessionMap.values()) {
            webSocketSession.sendMessage(new TextMessage(payload));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료되면 세션을 맵에서 제거
        String sessionId = session.getId();
        sessionMap.remove(sessionId);
        log.info("세션 종료: {}", sessionId);
    }
}
