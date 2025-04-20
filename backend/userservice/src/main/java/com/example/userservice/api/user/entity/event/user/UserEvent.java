package com.example.userservice.api.user.entity.event.user;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.example.userservice.api.user.entity.User;
import com.example.userservice.api.user.entity.User.UserRole;
import com.example.userservice.api.user.entity.User.UserStatus;
import com.example.userservice.common.enums.EventType;

import lombok.Getter;

@Getter
public class UserEvent extends ApplicationEvent {

    private final UUID id;
    private final UserRole role;
    private final UserStatus status;

    private final String nickName;

    private final EventType eventType;

    public UserEvent(Object source, User user, EventType eventType) {
        super(source);
        this.id = user.getId();
        this.role = user.getRole();
        this.status = user.getStatus();

        this.nickName = user.getNickName();

        this.eventType = eventType;
    }

}
