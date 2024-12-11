# WebSocket Chat Application

WebSocket과 STOMP를 사용한 실시간 채팅 애플리케이션입니다.

## 웹소켓(WebSocket)이란?
WebSocket은 클라이언트와 서버 간의 양방향 통신을 지원하는 프로토콜입니다.

### 기존 HTTP 통신 방식들

#### 1. 일반 HTTP 통신
- **특징**: 클라이언트의 요청이 있어야만 서버가 응답
- **단점**: 실시간성이 떨어지고 서버 부하 발생
````
Client ----Request----> Server
Client <---Response---- Server
````

#### 2. 폴링(Polling)
- **특징**: 클라이언트가 주기적으로 서버에 요청
- **단점**: 불필요한 요청이 많고 서버 부하 큼
````
Client ----Request-----> Server
Client <---Response----- Server
Client ----Request-----> Server
Client <---Response----- Server
(주기적 반복)
````

#### 3. 롱 폴링(Long Polling)
- **특징**: 클라이언트 요청을 서버가 보류하고 있다가 데이터 발생 시 응답
- **단점**: 많은 클라이언트 연결 시 서버 부하
````
Client ----Request----------------> Server
Client <---Response(데이터 발생 시)-- Server
````

### WebSocket 통신 방식

1. **Handshake (연결 수립)**
   - WebSocket 연결은 HTTP 프로토콜을 사용하여 초기화됩니다.
   - 클라이언트가 서버에 HTTP 요청을 보내고, 이 요청은 `Upgrade` 헤더를 포함하여 WebSocket 프로토콜로의 업그레이드를 요청합니다.
   - 서버가 이 요청을 수락하면, HTTP 연결이 WebSocket 연결로 업그레이드됩니다.
   - 이 과정이 완료되면 양방향 통신 채널이 열립니다.

2. **데이터 프레임 전송**
   - WebSocket은 텍스트와 바이너리 데이터를 전송할 수 있는 프레임 기반의 프로토콜입니다.
   - 데이터는 작은 오버헤드로 전송되며, 이는 실시간 애플리케이션에 적합합니다.
   - 클라이언트와 서버는 서로에게 자유롭게 메시지를 보낼 수 있습니다.

3. **프레임 구조**
   - WebSocket 프레임은 데이터의 시작과 끝을 나타내는 구조를 가지고 있습니다.
   - 각 프레임은 데이터 유형(텍스트, 바이너리), 길이, 그리고 실제 데이터로 구성됩니다.

4. **연결 유지**
   - WebSocket 연결은 지속적이며, 클라이언트와 서버 간의 연결이 명시적으로 종료될 때까지 유지됩니다.
   - 이는 지속적인 데이터 스트림을 필요로 하는 애플리케이션에 이상적입니다.

5. **연결 종료**
   - 클라이언트와 서버는 모두 연결을 종료할 수 있습니다.
   - 연결 종료는 `Close` 프레임을 통해 수행되며, 이는 연결이 정상적으로 종료되었음을 나타냅니다.
   - 연결 종료 시, 양측은 더 이상 데이터를 전송할 수 없습니다.

````
Client <----Data-----> Server
````

### WebSocket의 장점
1. **효율성**
   - 한 번 연결 수립 후 계속 사용
   - 헤더 오버헤드 감소
   - 실시간 데이터 전송 가능

2. **실시간성**
   - 지연 없는 양방향 통신
   - 서버에서 클라이언트로 즉시 푸시 가능

3. **자원 절약**
   - 불필요한 요청/응답 없음
   - 서버 부하 감소
   - 네트워크 트래픽 감소

## WebSocket 프로토콜 종류

### 1. STOMP (Simple Text Oriented Messaging Protocol)
- **특징**: HTTP와 유사한 프레임 기반 프로토콜
- **장점**: 
  - 메시지 형식이 정형화되어 있음
  - pub/sub 패턴 지원
  - Spring에서 기본 지원
- **사용 사례**: 채팅, 메시징 시스템

### 2. MQTT (Message Queuing Telemetry Transport)
- **특징**: 경량화된 pub/sub 메시징 프로토콜
- **장점**:
  - 낮은 대역폭 환경에 최적화
  - QoS(Quality of Service) 레벨 지원
  - 작은 메시지 크기
- **사용 사례**: IoT 디바이스, 센서 네트워크

### 3. Socket.IO
- **특징**: WebSocket을 추상화한 JavaScript 라이브러리
- **장점**:
  - 자동 재연결
  - 폴백 메커니즘 내장
  - 이벤트 기반 통신
- **사용 사례**: 실시간 게임, 협업 도구

### 4. Raw WebSocket
- **특징**: 프로토콜 없이 WebSocket API 직접 사용
- **장점**:
  - 최소한의 오버헤드
  - 커스텀 프로토콜 구현 가능
  - 가장 빠른 성능
- **사용 사례**: 저지연이 중요한 실시간 애플리케이션


## STOMP(Simple Text Oriented Messaging Protocol)
STOMP는 WebSocket 위에서 동작하는 메시징 프로토콜입니다. HTTP와 유사한 형식을 가집니다.

### STOMP 메시지 구조 예시

1. **연결 시 메시지**
````
CONNECT
accept-version:1.1,1.0
heart-beat:10000,10000

^@
````

2. **구독 메시지**
````
SUBSCRIBE
id:sub-0
destination:/topic/public

^@
````

3. **채팅 메시지 전송**
````
SEND
destination:/app/chat.sendMessage
content-type:application/json
content-length:58

{"type":"CHAT","content":"안녕하세요","sender":"user1"}^@
````

4. **서버에서 클라이언트로 메시지 전달**
````
MESSAGE
subscription:sub-0
message-id:123
destination:/topic/public
content-type:application/json

{"type":"CHAT","content":"안녕하세요","sender":"user1"}^@
````

### 구조 설명
- 첫 줄: 명령어(CONNECT, SUBSCRIBE, SEND, MESSAGE 등)
- 헤더: key:value 형식의 여러 줄
- 빈 줄: 헤더와 본문 구분
- 본문: JSON 등의 실제 데이터
- ^@: NULL 문자(메시지 종료 표시)

## Spring WebSocket 특징

### 1. Spring의 WebSocket 지원
- **@EnableWebSocketMessageBroker**: WebSocket 메시징 기능을 활성화
- **WebSocketMessageBrokerConfigurer**: WebSocket 설정을 위한 인터페이스 제공
- **메시지 브로커 옵션**:
  - **내장 브로커**: Simple Message Broker를 기본 제공
  - **외장 브로커**: RabbitMQ, ActiveMQ 등 연동 가능
    - 메시지 지속성
    - 클러스터링
    - 고가용성
    - 대규모 트래픽 처리

### 2. 메시지 핸들링
- **@MessageMapping**: WebSocket 메시지를 처리할 메서드 지정
- **@SendTo**: 메시지를 특정 주제로 발행
- **@SendToUser**: 특정 사용자에게만 메시지 전송
- **@DestinationVariable**: URL 템플릿 변수 바인딩

### 3. 보안 기능
- Spring Security와 통합 가능
- 사용자 인증/인가 처리
- CORS(Cross-Origin Resource Sharing) 설정

### 4. SockJS 폴백
- WebSocket을 지원하지 않는 환경에서 대체 전송 지원
- XHR Streaming
- XHR Polling
- 자동으로 최적의 전송 방식 선택

### 5. 세션 관리
- **WebSocketSession**: 클라이언트 연결 관리
- 세션 속성 저장 및 조회
- 연결 상태 모니터링

### 6. 에러 핸들링
- **@MessageExceptionHandler**: 메시지 처리 중 발생한 예외 처리
- 클라이언트에게 에러 메시지 전송
- 예외 유형별 처리 가능

## 프로젝트 아키텍처
````
Client (React) <--WebSocket/STOMP--> Server (Spring Boot)
     ↓                                    ↓
SockJS/StompJS                    Spring WebSocket
````

## 기술 스택

### Backend
- Spring Boot 3.2.1
- Spring WebSocket
- STOMP
- Gradle

### Frontend
- React
- SockJS-client
- @stomp/stompjs
- Node.js

## 프로젝트 구조
````
spring-stomp-practice/
├── docker-compose.yml
├── spring-stomp-practice-back/  # 백엔드
└── spring-stomp-practice-front/ # 프론트엔드
````

## 실행 방법

### Docker Compose 사용
````
# 프로젝트 클론
git clone https://github.com/your-username/spring-stomp-practice.git
cd spring-stomp-practice

# Docker Compose로 실행
docker-compose up --build
````

### 수동 실행
#### Backend
````
cd spring-stomp-practice-back
./gradlew bootRun
````

#### Frontend
````
cd spring-stomp-practice-front
npm install
npm start
````

## 접속 방법
- Frontend: http://localhost:3000 (개발 환경) 또는 http://localhost (Docker)
- Backend: http://localhost:8080

## 주요 기능
- 실시간 채팅
- 사용자 입장/퇴장 알림
- 자동 스크롤

## 구독 주제(Topics)
- `/topic/public`: 공개 채팅방 메시지

## 메시지 전송 경로
- `/app/chat.sendMessage`: 채팅 메시지 전송
- `/app/chat.addUser`: 사용자 입장
