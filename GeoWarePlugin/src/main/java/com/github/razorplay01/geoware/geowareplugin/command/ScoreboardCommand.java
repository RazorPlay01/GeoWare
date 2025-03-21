package com.github.razorplay01.geoware.geowareplugin.command;

import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreboardCommand implements CommandExecutor, TabCompleter {
    private final GeoWarePlugin plugin;

    public ScoreboardCommand() {
        this.plugin = GeoWarePlugin.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length < 8) {
            sender.sendMessage(Component.text("Uso: /2dgamescoreboard <target> <fadeInMs> <stayMs> <fadeOutMs> <offsetX> <offsetY> <scale> \"<list>\"").color(NamedTextColor.RED));
            sender.sendMessage(Component.text("Uso: /2dgamescoreboard <target> <fadeInMs> <stayMs> <fadeOutMs> <offsetX> <offsetY> <scale> auto").color(NamedTextColor.RED));
            return true;
        }

        String target = args[0];
        long fadeInMs, stayMs, fadeOutMs;
        int offsetX, offsetY;
        float scale;
        List<String> texts;

        try {
            fadeInMs = Long.parseLong(args[1]);
            stayMs = Long.parseLong(args[2]);
            fadeOutMs = Long.parseLong(args[3]);
            offsetX = Integer.parseInt(args[4]);
            offsetY = Integer.parseInt(args[5]);
            scale = Float.parseFloat(args[6]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Error: Los valores numéricos no son válidos").color(NamedTextColor.RED));
            return true;
        }

        String lastArg = args[7];
        if (lastArg.equalsIgnoreCase("auto")) {
            texts = plugin.getPointsManager().getTop12Players();
            while (texts.size() < 12) {
                texts.add("");
            }
            if (texts.size() > 12) {
                texts = texts.subList(0, 12);
            }
        } else {
            String listRaw = String.join(" ", Arrays.copyOfRange(args, 7, args.length));
            if (!listRaw.startsWith("\"") || !listRaw.endsWith("\"")) {
                sender.sendMessage(Component.text("Error: La lista debe estar entre comillas o usar 'auto'").color(NamedTextColor.RED));
                return true;
            }

            String listContent = listRaw.substring(1, listRaw.length() - 1);
            texts = new ArrayList<>(Arrays.asList(listContent.split(",")));
            for (int i = 0; i < texts.size(); i++) {
                texts.set(i, texts.get(i).trim());
            }
            while (texts.size() < 12) {
                texts.add("");
            }
        }

        List<Player> targets = new ArrayList<>();
        switch (target.toLowerCase()) {
            case "all":
                targets.addAll(Bukkit.getOnlinePlayers());
                break;
            case "allnotop":
                targets.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !p.isOp())
                        .toList());
                break;
            case "adventure":
                targets.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getGameMode() == GameMode.ADVENTURE)
                        .toList());
                break;
            case "survival":
                targets.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getGameMode() == GameMode.SURVIVAL)
                        .toList());
                break;
            case "spectator":
                targets.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getGameMode() == GameMode.SPECTATOR)
                        .toList());
                break;
            default:
                Player player = Bukkit.getPlayer(target);
                if (player != null && player.isOnline()) {
                    targets.add(player);
                } else {
                    sender.sendMessage(Component.text("Jugador no encontrado: " + target).color(NamedTextColor.RED));
                    return true;
                }
        }

        if (targets.isEmpty()) {
            sender.sendMessage(Component.text("No se encontraron jugadores para el target especificado").color(NamedTextColor.RED));
            return true;
        }

        for (Player targetPlayer : targets) {
            PacketSender.sendScoreboardPacketToClient(targetPlayer, texts, fadeInMs, stayMs, fadeOutMs, offsetX, offsetY, scale);
        }
        sender.sendMessage(Component.text("Scoreboard enviado a " + targets.size() + " jugador(es)").color(NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1: // Target
                completions.addAll(Arrays.asList("all", "allnotop", "adventure", "survival", "spectator"));
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList());
                break;
            case 2: // fadeInMs
                completions.addAll(Arrays.asList("500", "1000", "1500", "2000"));
                break;
            case 3: // stayMs
                completions.addAll(Arrays.asList("2000", "3000", "5000", "10000"));
                break;
            case 4: // fadeOutMs
                completions.addAll(Arrays.asList("500", "1000", "1500", "2000"));
                break;
            case 5: // offsetX
                completions.addAll(Arrays.asList("0", "10", "20", "-10", "-20"));
                break;
            case 6: // offsetY
                completions.addAll(Arrays.asList("0", "10", "20", "-10", "-20"));
                break;
            case 7: // scale
                completions.addAll(Arrays.asList("1.0", "1.5", "2.0", "0.5"));
                break;
            case 8: // list o auto
                if (!args[7].startsWith("\"")) {
                    completions.add("\"");
                    completions.add("auto");
                }
                break;
        }

        // Filtrar basándose en lo que ya se escribió
        if (args.length > 0) {
            String currentArg = args[args.length - 1].toLowerCase();
            completions = completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(currentArg))
                    .collect(Collectors.toList());
        }

        return completions;
    }
}