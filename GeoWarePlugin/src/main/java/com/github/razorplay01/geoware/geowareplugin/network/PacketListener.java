package com.github.razorplay01.geoware.geowareplugin.network;

import com.github.razorplay.packet_handler.exceptions.PacketInstantiationException;
import com.github.razorplay.packet_handler.exceptions.PacketSerializationException;
import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay.packet_handler.network.PacketTCP;
import com.github.razorplay01.geoware.geowarecommon.network.packet.ScorePacket;
import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static com.github.razorplay01.geoware.geowarecommon.GeoWareCommon.PACKET_BASE_CHANNEL;

public class PacketListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (channel.equals(PACKET_BASE_CHANNEL)) {
            ByteArrayDataInput buf = ByteStreams.newDataInput(message);

            try {
                IPacket packet = PacketTCP.read(buf);
                GeoWarePlugin.getInstance().getLogger().info("Packet received from the user: " + player.getName() + ", type: " + packet.getPacketId());
                if (packet instanceof ScorePacket pkt) {
                    int score = pkt.getScore();
                    GeoWarePlugin.getInstance().getPointsManager().addPoints(player, score);
                }
            } catch (PacketSerializationException | PacketInstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
