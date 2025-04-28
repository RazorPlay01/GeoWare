package com.github.razorplay01.geoware.geowareplugin;

import co.aikar.commands.PaperCommandManager;
import com.github.razorplay01.geoware.geowarecommon.GeoWareCommon;
import com.github.razorplay01.geoware.geowareplugin.api.GeoWarePluginAPI;
import com.github.razorplay01.geoware.geowareplugin.api.PointsManagerAPI;
import com.github.razorplay01.geoware.geowareplugin.command.*;
import com.github.razorplay01.geoware.geowareplugin.network.PacketListener;
import com.github.razorplay01.geoware.geowareplugin.util.UtilMessage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static com.github.razorplay01.geoware.geowarecommon.GeoWareCommon.PACKET_BASE_CHANNEL;


@Getter
public final class GeoWarePlugin extends JavaPlugin implements GeoWarePluginAPI {
    public static final String PLUGIN_NAME = "GeoWarePlugin";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);
    private PaperCommandManager commandManager;
    @Getter
    private static GeoWarePlugin instance;
    private PointsManager pointsManager;

    @Override
    public void onEnable() {
        instance = this;
        GeoWareCommon.registerPackets();

        try {
            pointsManager = new PointsManager(getDataFolder(), this);
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
        registerCommands();
        registerPacketChannels();

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

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new TwoDGameCommand());

        PointsCommand gamePointsCommand = new PointsCommand();
        getCommand("2dgamepoints").setExecutor(gamePointsCommand);
        getCommand("2dgamepoints").setTabCompleter(gamePointsCommand);

        ScoreboardCommand scoreboardCommand = new ScoreboardCommand();
        getCommand("2dgamescoreboard").setExecutor(scoreboardCommand);
        getCommand("2dgamescoreboard").setTabCompleter(scoreboardCommand);

        EmoteCommand emoteCommand = new EmoteCommand();
        getCommand("2dgamesemote").setExecutor(emoteCommand);
        getCommand("2dgamesemote").setTabCompleter(emoteCommand);

        TwoDGameScoreCommand scoreCommand = new TwoDGameScoreCommand();
        getCommand("2dgamescore").setExecutor(scoreCommand);
        getCommand("2dgamescore").setTabCompleter(scoreCommand);
    }

    @Override
    public PointsManagerAPI getPointsManagerAPI() {
        return pointsManager;
    }
}
