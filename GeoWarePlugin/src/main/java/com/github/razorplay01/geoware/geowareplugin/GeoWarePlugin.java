package com.github.razorplay01.geoware.geowareplugin;

import com.github.razorplay01.geoware.geowarecommon.GeoWareCommon;
import com.github.razorplay01.geoware.geowareplugin.command.PointsCommand;
import com.github.razorplay01.geoware.geowareplugin.command.TwoDGameCommand;
import com.github.razorplay01.geoware.geowareplugin.network.PacketListener;
import com.github.razorplay01.geoware.geowareplugin.util.UtilMessage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static com.github.razorplay01.geoware.geowarecommon.GeoWareCommon.PACKET_BASE_CHANNEL;


@Getter
public final class GeoWarePlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "GeoWarePlugin";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);

    private PointsManager pointsManager;

    @Override
    public void onEnable() {
        GeoWareCommon.registerPackets();
        registerCommands();
        registerPacketChannels();

        try {
            pointsManager = new PointsManager(getDataFolder());
            getLogger().info("Sistema de puntos iniciado correctamente.");
        } catch (SQLException e) {
            getLogger().severe("Error al iniciar la base de datos: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PointsPlaceholderExpansion().register();
            getLogger().info("Expansión de PlaceholderAPI registrada correctamente.");
        } else {
            getLogger().warning("PlaceholderAPI no encontrado. Los placeholders no estarán disponibles.");
        }

        UtilMessage.sendStartupMessage(this);
    }

    private void registerPacketChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, PACKET_BASE_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PACKET_BASE_CHANNEL, new PacketListener());
    }

    @Override
    public void onDisable() {
        if (pointsManager != null) {
            pointsManager.closeConnection();
            getLogger().info("Conexión a la base de datos cerrada.");
        }
        UtilMessage.sendShutdownMessage(this);
    }

    public static GeoWarePlugin getInstance() {
        return getPlugin(GeoWarePlugin.class);
    }

    private void registerCommands() {
        TwoDGameCommand gameCommand = new TwoDGameCommand();
        getCommand("2dgame").setExecutor(gameCommand);
        getCommand("2dgame").setTabCompleter(gameCommand);

        PointsCommand gamePointsCommand = new PointsCommand();
        getCommand("2dgamepoints").setExecutor(gamePointsCommand);
        getCommand("2dgamepoints").setTabCompleter(gamePointsCommand);
    }
}
