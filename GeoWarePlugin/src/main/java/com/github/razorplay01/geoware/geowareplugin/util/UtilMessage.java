package com.github.razorplay01.geoware.geowareplugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import static com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin.PLUGIN_NAME;

    public class UtilMessage {
    private static final Component DIVIDER = Component.text("----------------------------------------", NamedTextColor.GRAY);
    private static final Component PREFIX = Component.text()
            .append(Component.text("[", NamedTextColor.GRAY))
            .append(Component.text(PLUGIN_NAME, NamedTextColor.GOLD))
            .append(Component.text("] ", NamedTextColor.GRAY))
            .append(Component.text("» ", NamedTextColor.DARK_GRAY))
            .build();

    private UtilMessage() {
        //[]
    }

    public static void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(PREFIX.append(message));
    }

    public static void sendMessage(CommandSender sender, String miniMessage) {
        sender.sendMessage(PREFIX.append(MiniMessage.miniMessage().deserialize(miniMessage)));
    }

    public static void sendConsoleMessage(Component message, CommandSender sender) {
        sender.sendMessage(PREFIX.append(message));
    }

    public static void sendStartupMessage(JavaPlugin plugin) {
        var pluginMeta = plugin.getPluginMeta();

        Component[] messages = {
                DIVIDER,
                PREFIX.append(Component.text()
                        .append(Component.text(pluginMeta.getName(), NamedTextColor.WHITE))
                        .append(Component.text(" plugin Enabled!", NamedTextColor.GREEN))),
                PREFIX.append(Component.text()
                        .append(Component.text("Version: ", NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(pluginMeta.getVersion(), NamedTextColor.AQUA))),
                PREFIX.append(Component.text()
                        .append(Component.text("Developed by: ", NamedTextColor.WHITE))
                        .append(Component.text(String.join(", ", pluginMeta.getAuthors()), NamedTextColor.RED))),
                DIVIDER
        };

        for (Component message : messages) {
            plugin.getServer().getConsoleSender().sendMessage(message);
        }
    }

    public static void broadcastMessage(Component message) {
        @Nullable Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        assert plugin != null;
        plugin.getServer().broadcast(PREFIX.append(message));
    }

    public static void sendShutdownMessage(JavaPlugin plugin) {
        Component[] messages = {
                DIVIDER,
                PREFIX.append(Component.text()
                        .append(Component.text(plugin.getPluginMeta().getName(), NamedTextColor.WHITE))
                        .append(Component.text(" plugin Disabled!", NamedTextColor.RED))),
                DIVIDER
        };

        for (Component message : messages) {
            plugin.getServer().getConsoleSender().sendMessage(message);
        }
    }

    public static Component formatMessage(String text, NamedTextColor color) {
        return Component.text(text, color);
    }

    public static Component parseMiniMessage(String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }
}
