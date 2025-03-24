package com.github.razorplay01.geoware.geowareplugin.command;

import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EmoteCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION = "geoware.2dgamesemote";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUso: /2dgamesemote <target> \"<emoteId>\"");
            return true;
        }

        String target = args[0];
        String emoteId = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Collection<Player> targets = getTargetPlayers(target);

        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return true;
        }

        Emote emote = Emote.fromString(emoteId);
        if (emote == null) {
            sender.sendMessage("§cEmote ID inválido. Usa el autocompletado para ver opciones válidas. Recuerda usar comillas.");
            return true;
        }

        for (Player player : targets) {
            PacketSender.sendEmotePacketToClient(player, emoteId);
        }
        sender.sendMessage("§aEmote " + emoteId + " enviado a " + targets.size() + " jugador(es)");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Completar targets
            completions.add("all");
            completions.add("allnotop");
            completions.add("adventure");
            completions.add("survival");
            completions.add("spectator");
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        } else if (args.length >= 2) {
            // Completar emoteIds con comillas desde el enum
            String partialEmote = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase();
            if (!partialEmote.startsWith("\"")) {
                partialEmote = "\"" + partialEmote;
            }
            String finalPartialEmote = partialEmote;
            completions.addAll(Arrays.stream(Emote.values())
                    .map(Emote::getEmoteId)
                    .filter(emote -> emote.toLowerCase().startsWith(finalPartialEmote))
                    .toList());
        }

        return completions;
    }

    private Collection<Player> getTargetPlayers(String target) {
        switch (target.toLowerCase()) {
            case "all":
                return (Collection<Player>) Bukkit.getOnlinePlayers();
            case "allnotop":
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !player.isOp())
                        .collect(Collectors.toList());
            case "adventure":
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getGameMode() == GameMode.ADVENTURE)
                        .collect(Collectors.toList());
            case "survival":
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                        .collect(Collectors.toList());
            case "spectator":
                return Bukkit.getOnlinePlayers().stream()
                        .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                        .collect(Collectors.toList());
            default:
                Player player = Bukkit.getPlayer(target);
                return player != null && player.isOnline() ?
                        java.util.Collections.singletonList(player) :
                        java.util.Collections.emptyList();
        }
    }
}