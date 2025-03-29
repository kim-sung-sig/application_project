package com.example.userservice.common.util;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

public class UUIDv7Generator {

    public static UUID generate() {
        return UuidCreator.getTimeOrdered(); // UUIDv7
    }

}
