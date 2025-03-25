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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table
@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 채팅방 ID

    @Column(nullable = false)
    private String name; // 채팅방 이름

    @Column(nullable = false)
    private boolean isGroup; // 그룹 채팅 여부
    
    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성 날짜

    public static ChatRoom create(String name, boolean isGroup) {
        return ChatRoom.builder()
                .id(UUID.randomUUID())
                .name(name)
                .isGroup(isGroup)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
