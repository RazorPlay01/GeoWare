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
public class KeyBindPacket implements IPacket {
    private int score;
    private int timeLimitSeconds;
    private float circleSpeed;
    private float spawnChance;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.score = serializer.readInt();
        this.timeLimitSeconds = serializer.readInt();
        this.circleSpeed = serializer.readFloat();
        this.spawnChance = serializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeInt(this.score);
        serializer.writeInt(this.timeLimitSeconds);
        serializer.writeFloat(this.circleSpeed);
        serializer.writeFloat(this.spawnChance);
    }


    @Override
    public String getPacketId() {
        return "KeyBindPacket";
    }
}
