package com.example.springstomppracticeback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 메시지의 데이터 모델 클래스
 * WebSocket을 통해 주고받을 메시지의 형식을 정의합니다.
 */
@Data               // Lombok: getter, setter, toString, equals, hashCode 메서드 자동 생성
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자 자동 생성
public class ChatMessage {
    
    /**
     * 메시지 타입을 정의하는 열거형
     * 메시지의 성격에 따라 구분하여 처리하기 위해 사용
     */
    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 사용자 입장 메시지
        LEAVE    // 사용자 퇴장 메시지
    }

    private MessageType type;    // 메시지 타입 (CHAT, JOIN, LEAVE)
    private String content;      // 메시지 내용
    private String sender;       // 발신자 이름
    private String timestamp;    // 메시지 전송 시간

    /**
     * 메시지 생성 시각을 현재 시간으로 설정
     */
    public void setCurrentTimestamp() {
        this.timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
} 