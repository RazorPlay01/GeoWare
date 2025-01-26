package com.github.razorplay01.geoware.geowarecommon.exceptions;

import java.util.Objects;

public class PacketSerializationException extends Exception {
    public PacketSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PacketSerializationException(String message) {
        super(Objects.requireNonNull(message, "Message cannot be null"));
    }
}