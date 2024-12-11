package com.example.springstomppracticeback.controller;

import com.example.springstomppracticeback.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 채팅 메시지를 처리하는 컨트롤러
 * 클라이언트로부터 메시지를 수신하고, 처리된 메시지를 다시 클라이언트들에게 브로드캐스트합니다.
 */
@Controller
public class ChatController {

    /**
     * 클라이언트로부터 채팅 메시지를 수신하고 브로드캐스트하는 메서드
     * "/app/chat.sendMessage"로 메시지를 수신하고, "/topic/public"으로 메시지를 발송합니다.
     *
     * @param chatMessage 클라이언트가 전송한 채팅 메시지
     * @return 브로드캐스트할 채팅 메시지
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setCurrentTimestamp();  // 메시지 시간 설정
        return chatMessage;
    }

    /**
     * 새로운 사용자가 채팅방에 참여했을 때의 메시지를 처리하는 메서드
     * "/app/chat.addUser"로 메시지를 수신하고, "/topic/public"으로 메시지를 발송합니다.
     *
     * @param chatMessage 사용자 참여 메시지
     * @param headerAccessor 세션 관리를 위한 헤더 접근자
     * @return 브로드캐스트할 입장 메시지
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                             SimpMessageHeaderAccessor headerAccessor) {
        // 웹소켓 세션에 사용자 이름을 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setCurrentTimestamp();  // 메시지 시간 설정
        return chatMessage;
    }
} 