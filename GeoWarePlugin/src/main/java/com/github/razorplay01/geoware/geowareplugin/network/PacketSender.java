package com.github.razorplay01.geoware.geowareplugin.network;

import com.github.razorplay.packet_handler.exceptions.PacketSerializationException;
import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.PacketTCP;
import com.github.razorplay01.geoware.geowarecommon.network.packet.*;
import com.github.razorplay01.geoware.geowarecommon.util.Pair;
import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import com.github.razorplay01.geoware.geowareplugin.command.Emote;
import org.bukkit.entity.Player;

import java.util.List;

import static com.github.razorplay01.geoware.geowarecommon.GeoWareCommon.PACKET_BASE_CHANNEL;

public class PacketSender {
    private PacketSender() {
        //[]
    }

    public static void sendEmotePacketToClient(Player targetPlayer, String emoteId) {
        try {
            IPacket packet = new EmotePacket(emoteId);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendEmote(Player targetPlayer, Emote emote) {
        sendEmotePacketToClient(targetPlayer, emote.getEmoteId());
    }

    public static void sendArkanoidPacketToClient(Player targetPlayer, int timeLimitSeconds, int level) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new ArkanoidPacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendBubblePuzzlePacketToClient(Player targetPlayer, int timeLimitSeconds, int level) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new BubblePuzzlePacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendDonkeyKongPacketToClient(Player targetPlayer, int timeLimitSeconds, int spawnInterval, float spawnProbability) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new DonkeyKongPacket(prevScore, timeLimitSeconds, spawnInterval, spawnProbability);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendFruitFocusPacketToClient(Player targetPlayer, int timeLimitSeconds) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new FruitFocusPacket(prevScore, timeLimitSeconds);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendGalagaPacketToClient(Player targetPlayer, int timeLimitSeconds, int level) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new GalagaPacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendHanoiTowersPacketToClient(Player targetPlayer, int timeLimitSeconds, int rings) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new HanoiTowersPacket(prevScore, timeLimitSeconds, rings);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendKeyBindPacketToClient(Player targetPlayer, int timeLimitSeconds) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new KeyBindPacket(prevScore, timeLimitSeconds);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendRobotFactoryPacketToClient(Player targetPlayer, int timeLimitSeconds, float speedMultiplier, boolean enableRotation, int partQuantity) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new RobotFactoryPacket(prevScore, timeLimitSeconds, speedMultiplier, enableRotation, partQuantity);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendScaryMazePacketToClient(Player targetPlayer, int timeLimitSeconds, int level) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new ScaryMazePacket(prevScore, timeLimitSeconds, level);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendTetrisPacketToClient(Player targetPlayer, int timeLimitSeconds, float speedMultiplier) {
        try {
            int prevScore = GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(targetPlayer);
            IPacket packet = new TetrisPacket(prevScore, timeLimitSeconds, speedMultiplier);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendScoreboardPacketToClient(Player targetPlayer, List<String> texts, long fadeInMs, long stayMs, long fadeOutMs, int offsetX, int offsetY, float scale) {
        try {
            IPacket packet = new ScoreboardPacket(texts, fadeInMs, stayMs, fadeOutMs, offsetX, offsetY, scale);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendScoreStatusPacketToClient(Player targetPlayer, boolean isEnable) {
        try {
            IPacket packet = new ScoreStatusPacket(isEnable);
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    public static void sendScoreUpdaterPacketToClient(Player targetPlayer) {
        try {
            Pair<Integer, Integer> scoreAndPosition = GeoWarePlugin.getInstance().getPointsManager().obtenerPuntosYPosicion(targetPlayer);
            IPacket packet = new ScoreUpdaterPacket(scoreAndPosition.getKey(), scoreAndPosition.getValue());
            packetSendInfo(packet, targetPlayer);
            targetPlayer.sendPluginMessage(GeoWarePlugin.getInstance(), PACKET_BASE_CHANNEL, PacketTCP.write(packet));
        } catch (PacketSerializationException e) {
            GeoWarePlugin.getInstance().getLogger().warning(e.getMessage());
        }
    }

    private static void packetSendInfo(IPacket packet, Player targetPlayer) {
        GeoWarePlugin.getInstance().getLogger().info("Packet: " + packet.getPacketId() + ", sent to the client: " + targetPlayer.getName());
    }
}
