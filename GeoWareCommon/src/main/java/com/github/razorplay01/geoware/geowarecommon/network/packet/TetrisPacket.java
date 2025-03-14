package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketSerializationException;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.Packet;
import com.github.razorplay01.geoware.geowarecommon.network.network_util.PacketDataSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Packet
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TetrisPacket implements IPacket {
    private int score;
    private int timeLimitSeconds;
    private float speedMultiplier;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.score = serializer.readInt();
        this.timeLimitSeconds = serializer.readInt();
        this.speedMultiplier = serializer.readFloat();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeInt(this.score);
        serializer.writeInt(this.timeLimitSeconds);
        serializer.writeFloat(this.speedMultiplier);
    }


    @Override
    public String getPacketId() {
        return "TetrisPacket";
    }
}
