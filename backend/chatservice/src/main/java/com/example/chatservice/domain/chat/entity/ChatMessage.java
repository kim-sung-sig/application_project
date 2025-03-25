package com.example.chatservice.domain.chat.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    indexes = {
        @Index(name = "idx_chat_message_chatRoomId", columnList = "chatRoomId", unique = true),
    })
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
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
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .isSystemMessage(isSystemMessage)
                .sentAt(LocalDateTime.now())
                .build();
    }
}
