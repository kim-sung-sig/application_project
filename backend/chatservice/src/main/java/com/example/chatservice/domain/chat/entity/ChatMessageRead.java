package com.example.chatservice.domain.chat.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class ChatMessageRead {

    @Id
    private ChatMessageReadId id;

    @Column(nullable = false)
    private LocalDateTime readAt; // 읽은 시간

    public static ChatMessageRead create(ChatMessageReadId id) {
        return ChatMessageRead.builder()
                .id(id)
                .readAt(LocalDateTime.now())
                .build();
    }

}