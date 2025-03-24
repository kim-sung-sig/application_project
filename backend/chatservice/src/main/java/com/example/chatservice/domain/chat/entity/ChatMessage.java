package com.example.chatservice.domain.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 메시지 ID

    @Column(nullable = false)
    private UUID chatRoomId; // 채팅방 ID

    @Column(nullable = false)
    private UUID senderId; // 보낸 사람 ID

    @Column(nullable = false)
    private String content; // 메시지 내용

    @Column(nullable = false)
    private boolean isSystemMessage; // 시스템 메시지 여부

    @Column(nullable = false)
    private LocalDateTime sentAt; // 전송 시간

    public static ChatMessage create(UUID chatRoomId, UUID senderId, String content, boolean isSystemMessage) {
        return ChatMessage.builder()
                .id(UUID.randomUUID())
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .isSystemMessage(isSystemMessage)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
