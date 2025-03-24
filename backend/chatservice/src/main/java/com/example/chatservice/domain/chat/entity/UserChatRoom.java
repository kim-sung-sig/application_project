package com.example.chatservice.domain.chat.entity;

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

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "chatRoomId"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // ID

    @Column(nullable = false)
    private UUID userId; // 유저 ID (외래키)

    @Column(nullable = false)
    private UUID chatRoomId; // 채팅방 ID (외래키)

    public static UserChatRoom create(UUID userId, UUID chatRoomId) {
        return UserChatRoom.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .chatRoomId(chatRoomId)
                .build();
    }
}
