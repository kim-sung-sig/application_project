package com.example.chatservice.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.chatservice.application.chat.message.ReactiveMessageService;

import reactor.core.publisher.Mono;

@RestController
public class WebSocketController extends TextWebSocketHandler {

    private final ReactiveMessageService messageService;

    public WebSocketController(ReactiveMessageService messageService) {
        this.messageService = messageService;
    }

    public Mono<Void> handle(WebSocketSession session, WebSocketMessage<?> message) {
        String payload = (String) message.getPayload();
        return messageService.sendMessage(payload);
    }
}
