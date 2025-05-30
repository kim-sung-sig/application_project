package com.example.userservice.api.user.entity.event.user;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserEventHandler {

    @EventListener(classes = UserEvent.class)
    public void handleEvent(UserEvent event) {
        log.debug("UserEventHandler handleEvent");
    }

}
