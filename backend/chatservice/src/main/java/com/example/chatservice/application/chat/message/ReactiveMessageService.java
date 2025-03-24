package com.example.chatservice.application.chat.message;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ReactiveMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ReactiveMessageService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // 카프카에 메시지를 발행하는 서비스
    public Mono<Void> sendMessage(String message) {
        return Mono.fromRunnable(() -> {
            kafkaTemplate.send("chat-topic", message);
        });
    }
}
