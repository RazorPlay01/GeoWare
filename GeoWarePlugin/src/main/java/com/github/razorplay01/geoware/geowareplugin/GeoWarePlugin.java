package com.github.razorplay01.geoware.geowareplugin;

import com.github.razorplay01.geoware.geowarecommon.network.PacketTCP;
import com.github.razorplay01.geoware.geowareplugin.command.TwoDGameCommand;
import com.github.razorplay01.geoware.geowareplugin.network.PacketListener;
import com.github.razorplay01.geoware.geowareplugin.util.UtilMessage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Getter
public final class GeoWarePlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "GeoWarePlugin";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);

    @Override
    public void onEnable() {
        //Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        registerCommands();

        registerPacketChannels();
        UtilMessage.sendStartupMessage(this);
    }

    private void registerPacketChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_BASE_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_BASE_CHANNEL, new PacketListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_ARKANOID_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_ARKANOID_CHANNEL, new PacketListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_BUBBLEPUZZLE_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_BUBBLEPUZZLE_CHANNEL, new PacketListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_DONKEYKONG_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_DONKEYKONG_CHANNEL, new PacketListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_HANOITOWERS_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_HANOITOWERS_CHANNEL, new PacketListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_TETRIS_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_TETRIS_CHANNEL, new PacketListener());
    }

    @Override
    public void onDisable() {
        UtilMessage.sendShutdownMessage(this);
    }

    public static GeoWarePlugin getInstance() {
        return getPlugin(GeoWarePlugin.class);
    }

    private void registerCommands() {
        TwoDGameCommand command = new TwoDGameCommand();
        getCommand("2dgame").setExecutor(command);
        getCommand("2dgame").setTabCompleter(command);
    }
}
