package com.example.springstomppracticeback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 통신을 위한 설정 클래스
 * WebSocket은 클라이언트와 서버 간의 양방향 통신을 지원하는 프로토콜입니다.
 * STOMP는 WebSocket 위에서 동작하는 메시징 프로토콜로, 여러 기능을 추가로 제공합니다.
 */
@Configuration  // 스프링의 설정 클래스임을 표시
@EnableWebSocketMessageBroker  // WebSocket 메시지 처리를 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * MessageBroker를 설정하는 메소드
     * MessageBroker는 송신자로부터 받은 메시지를 수신자들에게 전달하는 중간 역할을 합니다.
     * 
     * @param config MessageBroker 설정을 위한 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // /topic으로 시작하는 메시지가 메시지 브로커로 라우팅되도록 정의
        // 메시지 브로커는 해당 토픽을 구독하는 모든 클라이언트에게 메시지를 전달합니다.
        // 예: /topic/chat, /topic/notification 등
        config.enableSimpleBroker("/topic");

        // 클라이언트에서 보낸 메시지를 받을 prefix 설정
        // /app으로 시작하는 메시지만 해당 핸들러로 라우팅됩니다.
        // 예: /app/chat.message, /app/chat.join 등
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * WebSocket 연결 엔드포인트를 등록하는 메소드
     * 클라이언트가 WebSocket 핸드셰이크 커넥션을 생성할 경로를 설정합니다.
     *
     * @param registry 엔드포인트 등록을 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")  // WebSocket 또는 SockJS 클라이언트가 웹소켓 핸드셰이크 커넥션을 생성할 경로
                .setAllowedOrigins("http://localhost:3000")   // 모든 오리진에서의 접속을 허용 (개발 환경용, 운영 환경에서는 구체적인 도메인 설정 필요)
                .withSockJS();            // SockJS 사용을 활성화
                                         // WebSocket을 지원하지 않는 브라우저에 대한 폴백 옵션을 활성화
    }
} 