package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.network_util.PacketDataSerializer;
import com.github.razorplay.packet_handler.exceptions.PacketSerializationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FruitFocusPacket implements IPacket {
    private int score;
    private int timeLimitSeconds;
    private int hideDurationSeconds;
    private int fruitsToHide;
    private int completePoint;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.score = serializer.readInt();
        this.timeLimitSeconds = serializer.readInt();
        this.hideDurationSeconds = serializer.readInt();
        this.fruitsToHide = serializer.readInt();
        this.completePoint = serializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeInt(this.score);
        serializer.writeInt(this.timeLimitSeconds);
        serializer.writeInt(this.hideDurationSeconds);
        serializer.writeInt(this.fruitsToHide);
        serializer.writeInt(this.completePoint);
    }


    @Override
    public String getPacketId() {
        return "FruitFocusPacket";
    }
}
