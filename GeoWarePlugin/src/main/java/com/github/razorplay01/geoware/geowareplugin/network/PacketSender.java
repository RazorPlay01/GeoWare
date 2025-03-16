package com.github.razorplay01.geoware.geowareplugin.network;

import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketSerializationException;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.PacketTCP;
import com.github.razorplay01.geoware.geowarecommon.network.packet.*;
import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import org.bukkit.entity.Player;

public class PacketSender {
    private PacketSender() {
        //[]
    }

    public static void sendArkanoidPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, int level) {
        try {
            IPacket packet = new ArkanoidPacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendBubblePuzzlePacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, int level) {
        try {
            IPacket packet = new BubblePuzzlePacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendDonkeyKongPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, int spawnInterval, float spawnProbability) {
        try {
            IPacket packet = new DonkeyKongPacket(prevScore, timeLimitSeconds, spawnInterval, spawnProbability);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_DONKEYKONG_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendFruitFocusPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds) {
        try {
            IPacket packet = new FruitFocusPacket(prevScore, timeLimitSeconds);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendGalagaPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, int level) {
        try {
            IPacket packet = new GalagaPacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendHanoiTowersPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, int rings) {
        try {
            IPacket packet = new HanoiTowersPacket(prevScore, timeLimitSeconds, rings);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendKeyBindPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds) {
        try {
            IPacket packet = new KeyBindPacket(prevScore, timeLimitSeconds);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendRobotFactoryPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds) {
        try {
            IPacket packet = new RobotFactoryPacket(prevScore, timeLimitSeconds);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendTetrisPacketToClient(Player targetPlayer, int prevScore, int timeLimitSeconds, float speedMultiplier) {
        try {
            IPacket packet = new TetrisPacket(prevScore, timeLimitSeconds, speedMultiplier);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PacketTCP.PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }


    private static void packetSendInfo(IPacket packet, Player targetPlayer) {
        GeoWarePlugin.getInstance().getLogger().info("Packet: " + packet.getPacketId() + ", sent to the client: " + targetPlayer.getName());
    }
}
