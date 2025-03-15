package com.github.razorplay01.geoware.geowareplugin.network;

import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketInstantiationException;
import com.github.razorplay01.geoware.geowarecommon.exceptions.PacketSerializationException;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.PacketTCP;
import com.github.razorplay01.geoware.geowarecommon.network.packet.ScorePacket;
import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PacketListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (channel.equals(PacketTCP.PACKET_BASE_CHANNEL)) {
            ByteArrayDataInput buf = ByteStreams.newDataInput(message);

            try {
                IPacket packet = PacketTCP.read(buf);
                GeoWarePlugin.getInstance().getLogger().info("Packet received from the user: " + player.getName() + ", type: " + packet.getPacketId());
                if (packet instanceof ScorePacket pkt) {
                    int score = pkt.getScore();
                    GeoWarePlugin.getInstance().getLogger().info("score: " + score);
                    GeoWarePlugin.getInstance().getPointsManager().sumarPuntos(player, score);
                }
            } catch (PacketSerializationException | PacketInstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
