package com.example.chatservice.domain.chat.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter @ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageReadId implements Serializable {

    private UUID userId;

    private UUID chatMessageId;

}
