package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay.packet_handler.exceptions.PacketSerializationException;
import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.network_util.PacketDataSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreUpdaterPacket implements IPacket {
    private int score;
    private int position;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.score = serializer.readInt();
        this.position = serializer.readInt();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeInt(this.score);
        serializer.writeInt(this.position);
    }


    @Override
    public String getPacketId() {
        return "ScoreUpdaterPacket";
    }
}
