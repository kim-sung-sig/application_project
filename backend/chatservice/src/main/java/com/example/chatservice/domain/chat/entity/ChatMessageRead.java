package com.example.chatservice.domain.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "chatMessageId"}))
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 읽음 ID

    @Column(nullable = false)
    private UUID userId; // 유저 ID (외래키)

    @Column(nullable = false)
    private UUID chatMessageId; // 메시지 ID (외래키)

    @Column(nullable = false)
    private LocalDateTime readAt; // 읽은 시간

    public static ChatMessageRead create(UUID userId, UUID chatMessageId) {
        return ChatMessageRead.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .chatMessageId(chatMessageId)
                .readAt(LocalDateTime.now())
                .build();
    }
}