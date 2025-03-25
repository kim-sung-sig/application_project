package com.example.chatservice.application.chat.message;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactiveMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "chat-topic";

    // 카프카에 메시지를 발행하는 서비스
    public Mono<Void> sendMessage(String message) {
        return Mono.fromRunnable(() -> {
            kafkaTemplate.send(TOPIC, message);
        });
    }
}
