package com.github.razorplay01.geoware.geowarecommon.exceptions;

import java.util.Objects;

public class PacketNotFoundException extends RuntimeException {
    public PacketNotFoundException(String message) {
        super(Objects.requireNonNull(message, "Message cannot be null"));
    }
}