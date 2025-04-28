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
public class DonkeyKongPacket implements IPacket {
    private int score;
    private int timeLimitSeconds;
    private int spawnInterval;
    private float spawnProbability;
    private int finalPoints;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.score = serializer.readInt();
        this.timeLimitSeconds = serializer.readInt();
        this.spawnInterval = serializer.readInt();
        this.spawnProbability = serializer.readFloat();
        this.finalPoints = serializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeInt(this.score);
        serializer.writeInt(this.timeLimitSeconds);
        serializer.writeInt(this.spawnInterval);
        serializer.writeFloat(this.spawnProbability);
        serializer.writeInt(this.finalPoints);
    }


    @Override
    public String getPacketId() {
        return "DonkeyKongPacket";
    }
}
