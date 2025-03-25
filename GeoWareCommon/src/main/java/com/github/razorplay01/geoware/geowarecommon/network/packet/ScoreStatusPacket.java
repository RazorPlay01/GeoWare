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
public class ScoreStatusPacket implements IPacket {
    private boolean isEnable;

    @Override
    public void read(PacketDataSerializer serializer) throws PacketSerializationException {
        this.isEnable = serializer.readBoolean();
    }

    @Override
    public void write(PacketDataSerializer serializer) throws PacketSerializationException {
        serializer.writeBoolean(this.isEnable);
    }


    @Override
    public String getPacketId() {
        return "ScoreStatusPacket";
    }
}
