package com.github.razorplay01.geoware.geowarecommon.network.network_util;

/**
 * Interface for custom objects that can be serialized and deserialized using PacketDataSerializer.
 */
public interface CustomSerializable {
    void serialize(PacketDataSerializer serializer);
    void deserialize(PacketDataSerializer serializer);
}