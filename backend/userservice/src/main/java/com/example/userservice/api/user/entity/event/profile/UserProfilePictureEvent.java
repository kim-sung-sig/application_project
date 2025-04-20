package com.example.userservice.api.user.entity.event.profile;

import java.util.UUID;

import org.springframework.context.ApplicationEvent;

import com.example.userservice.api.user.entity.UserProfilePicture;
import com.example.userservice.common.enums.EventType;

import lombok.Getter;

@Getter
public class UserProfilePictureEvent extends ApplicationEvent {

    // user
    private final UUID userId;

    // profile
    private final UUID id;
    private final String fileUrl;
    private final String status;

    // event 상태
    private final EventType eventType;

    public UserProfilePictureEvent(Object source, UserProfilePicture profile, EventType eventType) {
        super(source);
        this.userId = profile.getUserId();

        this.id = profile.getId();
        this.fileUrl = profile.getFileUrl();
        this.status = profile.getStatus().name();

        this.eventType = eventType;
    }

}
