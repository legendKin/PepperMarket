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

// WebSocket 메시지를 처리하는 핸들러 클래스
@Component // Spring 컴포넌트임을 나타내는 어노테이션
@Log4j2 // lombok을 사용하여 로그 기능 추가
public class ChatHandler extends TextWebSocketHandler {

    // 채팅 메시지 서비스를 주입받음
    private final ChatMessageService chatMessageService;

    // 사용자 세션을 관리하는 맵
    private final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    @Autowired // 의존성 주입을 위한 어노테이션
    public ChatHandler(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // WebSocket 메시지 수신 시 호출되는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // 메시지 페이로드 추출
        log.info("payload : " + payload); // 로그 출력
        String[] parts = payload.split(":", 2); // 최대 2개의 부분으로 분할
        if (parts.length < 2) {
            log.warn("Invalid message format: " + payload); // 유효하지 않은 메시지 형식이면 경고 로그 출력
            return; // 처리하지 않고 종료
        }
        String senderId = parts[0]; // 보낸 사용자의 ID
        String msg = parts[1]; // 메시지 내용

        // 채팅 메시지 객체 생성 및 저장
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(senderId); // 보낸 사용자 ID 설정
        chatMessage.setMessage(msg); // 메시지 내용 설정
        chatMessage.setTimestamp(LocalDateTime.now()); // 현재 시간으로 타임스탬프 설정
        chatMessageService.saveChatMessage(chatMessage); // 채팅 메시지 서비스를 통해 저장

        // 세션맵을 순회하며 메시지를 모든 사용자에게 브로드캐스트
        for (Map.Entry<String, WebSocketSession> entry : sessionMap.entrySet()) {
            if (!entry.getKey().equals(senderId)) { // 보낸 사용자를 제외하고
                entry.getValue().sendMessage(new TextMessage(senderId + ": " + msg)); // 메시지 전송
            }
        }
    }

    // WebSocket 연결이 수립된 후 호출되는 메서드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = session.getUri().getQuery().split("=")[1]; // 사용자 이름 추출
        sessionMap.put(username, session); // 세션맵에 사용자 세션 추가
        log.info(username + " 클라이언트 접속"); // 접속 로그 출력
    }

    // WebSocket 연결이 종료된 후 호출되는 메서드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = session.getUri().getQuery().split("=")[1]; // 사용자 이름 추출
        log.info(username + " 클라이언트 접속 해제"); // 접속 해제 로그 출력
        sessionMap.remove(username); // 세션맵에서 사용자 세션 제거
    }
}
