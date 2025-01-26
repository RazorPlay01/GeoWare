package com.github.razorplay01.geoware.geowarecommon.network.packet;

import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketSerializationException;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.util.ScreenCoords;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TextureScreenPacket implements IPacket {
    private double scale;
    private ScreenCoords screenCoords;
    private int u;
    private int v;
    private int width;
    private int height;
    private int textureWidth;
    private int textureHeight;
    private String textureId;
    private boolean canBeClose;

    @Override
    public void read(ByteArrayDataInput buf) throws PacketSerializationException {
        this.scale = buf.readDouble();
        this.screenCoords = ScreenCoords.valueOf(buf.readUTF());
        this.u = buf.readInt();
        this.v = buf.readInt();
        this.width = buf.readInt();
        this.height = buf.readInt();
        this.textureWidth = buf.readInt();
        this.textureHeight = buf.readInt();
        this.textureId = buf.readUTF();
        this.canBeClose = buf.readBoolean();
    }

    @Override
    public void write(ByteArrayDataOutput buf) throws PacketSerializationException {
        buf.writeDouble(this.scale);
        buf.writeUTF(this.screenCoords.name());
        buf.writeInt(this.u);
        buf.writeInt(this.v);
        buf.writeInt(this.width);
        buf.writeInt(this.height);
        buf.writeInt(this.textureWidth);
        buf.writeInt(this.textureHeight);
        buf.writeUTF(this.textureId);
        buf.writeBoolean(this.canBeClose);
    }

    @Override
    public String getPacketId() {
        return "TextureScreenPacket";
    }
}