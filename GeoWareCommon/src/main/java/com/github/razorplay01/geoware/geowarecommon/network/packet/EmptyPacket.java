package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketSerializationException;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyPacket implements IPacket {
    @Override
    public void read(ByteArrayDataInput buf) throws PacketSerializationException {
        //[]
    }

    @Override
    public void write(ByteArrayDataOutput buf) throws PacketSerializationException {
        //[]
    }

    @Override
    public String getPacketId() {
        return "EmptyPacket";
    }
}
