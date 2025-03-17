package com.github.razorplay01.geoware.geowareplugin;

import com.github.razorplay01.geoware.geowarecommon.network.PacketTCP;
import com.github.razorplay01.geoware.geowareplugin.command.TwoDGameCommand;
import com.github.razorplay01.geoware.geowareplugin.network.PacketListener;
import com.github.razorplay01.geoware.geowareplugin.util.UtilMessage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;


@Getter
public final class GeoWarePlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "GeoWarePlugin";
    public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);

    private PointsManager pointsManager;

    @Override
    public void onEnable() {
        registerCommands();
        registerPacketChannels();

        try {
            pointsManager = new PointsManager(getDataFolder());
            getLogger().info("Sistema de puntos iniciado correctamente.");
        } catch (SQLException e) {
            getLogger().severe("Error al iniciar la base de datos: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
        UtilMessage.sendStartupMessage(this);
    }

    private void registerPacketChannels() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, PacketTCP.PACKET_BASE_CHANNEL);
        getServer().getMessenger().registerIncomingPluginChannel(this, PacketTCP.PACKET_BASE_CHANNEL, new PacketListener());
    }

    @Override
    public void onDisable() {
        getLogger().info(pointsManager.topMejores().toString());

        if (pointsManager != null) {
            pointsManager.cerrarConexion();
            getLogger().info("Conexión a la base de datos cerrada.");
        }
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
