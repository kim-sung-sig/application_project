package com.example.userservice.api.user.domain.event.profile;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserProfilePrctureEventHandler {

    @EventListener(classes = UserProfilePictureEvent.class)
    public void handleEvent(UserProfilePictureEvent event) {
        log.debug("UserProfilePrctureEventHandler handleEvent");
    }
}
