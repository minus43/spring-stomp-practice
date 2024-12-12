import React, { useState, useRef, useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import './App.css';

function App() {
  const [connected, setConnected] = useState(false);
  const [username, setUsername] = useState('');
  const [message, setMessage] = useState('');
  const [messages, setMessages] = useState([]);
  const clientRef = useRef(null);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const connect = () => {
    if (!username.trim()) {
      alert('이름을 입력해주세요.');
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws-chat'),
      debug: function (str) {
        console.log(str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      setConnected(true);

      // 공개 채널 구독
      client.subscribe('/topic/public', (message) => {
        const receivedMessage = JSON.parse(message.body);
        setMessages((prevMessages) => [...prevMessages, receivedMessage]);
      });

      // 입장 메시지 전송
      client.publish({
        destination: '/app/chat.addUser',
        body: JSON.stringify({
          sender: username,
          type: 'JOIN',
        }),
      });
    };

    client.onDisconnect = () => {
      setConnected(false);
    };

    client.activate();
    clientRef.current = client;
  };

  const disconnect = () => {
    if (clientRef.current) {
      // 퇴장 메시지 전송
      clientRef.current.publish({
        destination: '/app/chat.addUser',
        body: JSON.stringify({
          sender: username,
          type: 'LEAVE'
        })
      });
      
      clientRef.current.deactivate();
    }
  };

  const sendMessage = (event) => {
    event.preventDefault();
    if (!message.trim() || !clientRef.current) return;

    clientRef.current.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify({
        sender: username,
        content: message,
        type: 'CHAT',
      }),
    });

    setMessage('');
  };

  return (
    <div className='chat-container'>
      <h1>WebSocket 채팅방</h1>

      {!connected ? (
        <div className='join-container'>
          <input
            type='text'
            placeholder='이름을 입력하세요'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && connect()}
          />
          <button onClick={connect}>참여하기</button>
        </div>
      ) : (
        <div className='chat-box'>
          <div className='messages'>
            {messages.map((msg, index) => (
              <div key={index} className={`message ${msg.type.toLowerCase()}`}>
                {msg.type === 'JOIN' ? (
                  `${msg.sender}님이 입장하셨습니다.`
                ) : msg.type === 'LEAVE' ? (
                  `${msg.sender}님이 퇴장하셨습니다.`
                ) : (
                  `${msg.sender}: ${msg.content}`
                )}
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>
          <form onSubmit={sendMessage} className='message-form'>
            <input
              type='text'
              placeholder='메시지를 입력하세요'
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />
            <button type='submit'>전송</button>
            <button type='button' onClick={disconnect}>나가기</button>
          </form>
        </div>
      )}
    </div>
  );
}

export default App;
