package com.example.demo.config;

// 필요한 클래스와 어노테이션을 가져옴
import com.example.demo.handler.ChatHandler; // 채팅 핸들러 클래스
import lombok.RequiredArgsConstructor; // 롬복의 @RequiredArgsConstructor 어노테이션을 사용하기 위해 가져옴
import org.springframework.context.annotation.Configuration; // 스프링의 @Configuration 어노테이션을 사용하기 위해 가져옴
import org.springframework.web.socket.WebSocketHandler; // WebSocketHandler 인터페이스를 사용하기 위해 가져옴
import org.springframework.web.socket.config.annotation.EnableWebSocket; // 웹소켓을 활성화하기 위해 가져옴
import org.springframework.web.socket.config.annotation.WebSocketConfigurer; // WebSocketConfigurer 인터페이스를 사용하기 위해 가져옴
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry; // WebSocketHandlerRegistry 클래스를 사용하기 위해 가져옴

// 이 클래스가 스프링 설정 클래스임을 나타내고 웹소켓을 활성화함
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // 웹소켓 핸들러를 주입 받기 위한 필드 선언
    private final WebSocketHandler webSocketHandler;

    // WebSocketConfig 클래스의 생성자
    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler; // 생성자로 주입 받은 웹소켓 핸들러를 필드에 할당
    }

    // WebSocketConfigurer 인터페이스의 메서드를 구현하여 웹소켓 핸들러를 등록
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // /ws/chat 경로에 대해 웹소켓 핸들러를 등록하고, 모든 출처에서의 접근을 허용
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOrigins("*");
    }
}
