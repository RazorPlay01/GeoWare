package com.github.razorplay01.geoware.geowarecommon;

import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.PacketTCP;
import com.github.razorplay01.geoware.geowarecommon.network.packet.*;

public class GeoWareCommon {
    public static final String PACKET_BASE_CHANNEL = "geoware:packets_channel";

    private GeoWareCommon() {
        //  []
    }

    public static void registerPackets() {
        Class<? extends IPacket>[] packetClasses = new Class[]{
                ArkanoidPacket.class,
                BubblePuzzlePacket.class,
                DonkeyKongPacket.class,
                EmptyPacket.class,
                FruitFocusPacket.class,
                GalagaPacket.class,
                HanoiTowersPacket.class,
                KeyBindPacket.class,
                RobotFactoryPacket.class,
                ScorePacket.class,
                ScoreStatusPacket.class,
                ScoreUpdaterPacket.class,
                TetrisPacket.class,
                ScaryMazePacket.class,
                ScoreboardPacket.class,
                EmotePacket.class
        };
        PacketTCP.registerPackets(packetClasses);
    }
}