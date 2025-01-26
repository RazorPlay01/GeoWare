package com.github.razorplay01.geoware.geowarecommon.exceptions;

import java.util.Objects;

public class PacketRegistrationException extends RuntimeException {
    public PacketRegistrationException(String message) {
        super(Objects.requireNonNull(message, "Message cannot be null"));
    }
}