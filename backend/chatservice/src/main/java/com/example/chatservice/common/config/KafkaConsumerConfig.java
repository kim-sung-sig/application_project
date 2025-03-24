package com.example.chatservice.common.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final String bootstrapServers = "localhost:9092"; // 카프카 서버 주소

    @Bean
    public MessageListenerContainer messageListenerContainer() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(configProps);

        // 메시지 리스너 정의
        MessageListener<String, String> messageListener = record -> {
            System.out.println("Received message: " + record.value());
        };

        // ContainerProperties 설정
        ContainerProperties containerProps = new ContainerProperties("chat-topic"); // 구독할 카프카 토픽 이름 설정
        containerProps.setMessageListener(messageListener);  // 메시지 리스너 설정

        // 메시지 리스너 컨테이너 설정
        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProps);
    }
}
