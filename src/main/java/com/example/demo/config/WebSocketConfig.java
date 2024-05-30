package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket을 활성화하고 WebSocketHandler를 등록하는 설정 클래스입니다.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    /**
     * WebSocketConfig 생성자.
     *
     * @param webSocketHandler WebSocket 핸들러
     */
    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * WebSocketHandler를 "/ws/chat" 엔드포인트에 매핑하고 모든 origin을 허용합니다.
     *
     * @param registry WebSocketHandler 레지스트리
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/chat").setAllowedOrigins("*");
    }
}
