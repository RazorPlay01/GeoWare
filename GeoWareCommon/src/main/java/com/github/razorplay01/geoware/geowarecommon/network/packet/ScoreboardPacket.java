package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay.packet_handler.exceptions.PacketSerializationException;
import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.network_util.PacketDataSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreboardPacket implements IPacket {
    private List<String> texts;
    private long fadeInMs;
    private long stayMs;
    private long fadeOutMs;
    private int offsetX;
    private int offsetY;
    private float scale;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.texts = serializer.readList(PacketDataSerializer::readString);
        this.fadeInMs = serializer.readLong();
        this.stayMs = serializer.readLong();
        this.fadeOutMs = serializer.readLong();
        this.offsetX = serializer.readInt();
        this.offsetY = serializer.readInt();
        this.scale = serializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeList(texts, PacketDataSerializer::writeString);
        serializer.writeLong(fadeInMs);
        serializer.writeLong(stayMs);
        serializer.writeLong(fadeOutMs);
        serializer.writeInt(offsetX);
        serializer.writeInt(offsetY);
        serializer.writeFloat(scale);
    }


    @Override
    public String getPacketId() {
        return "ScoreboardPacket";
    }
}
