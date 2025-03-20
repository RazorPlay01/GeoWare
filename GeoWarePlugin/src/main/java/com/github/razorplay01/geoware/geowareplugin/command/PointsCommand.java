package com.github.razorplay01.geoware.geowareplugin.command;

import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointsCommand implements CommandExecutor, TabCompleter {
    public PointsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Uso: /2dgamepoints <top10|add|subtract|reset> [opciones]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "top10":
                enviarTop10(sender);
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /2dgamepoints add <username> <amount>");
                    return true;
                }
                manejarAdd(sender, args[1], args[2]);
                break;

            case "subtract":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Uso: /2dgamepoints subtract <username> <amount>");
                    return true;
                }
                manejarSubtract(sender, args[1], args[2]);
                break;

            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Uso: /2dgamepoints reset <all|username>");
                    return true;
                }
                manejarReset(sender, args[1]);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Subcomando inválido. Usa: top10, add, subtract, reset");
                break;
        }
        return true;
    }

    private void enviarTop10(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Top 10 Mejores ===");
        List<String> topMejores = GeoWarePlugin.getInstance().getPointsManager().getTopPlayers();
        if (topMejores.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No hay jugadores con puntos aún.");
        } else {
            for (int i = 0; i < topMejores.size(); i++) {
                sender.sendMessage(ChatColor.GREEN + "#" + (i + 1) + " " + topMejores.get(i));
            }
        }

        sender.sendMessage(ChatColor.GOLD + "=== Top 10 Peores ===");
        List<String> topPeores = GeoWarePlugin.getInstance().getPointsManager().getBottomPlayers();
        if (topPeores.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No hay jugadores con puntos positivos aún.");
        } else {
            for (int i = 0; i < topPeores.size(); i++) {
                sender.sendMessage(ChatColor.RED + "#" + (i + 1) + " " + topPeores.get(i));
            }
        }
    }

    private void manejarAdd(CommandSender sender, String username, String amountStr) {
        Player target = Bukkit.getPlayerExact(username);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jugador '" + username + "' no encontrado o no está en línea.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "La cantidad debe ser un número entero positivo.");
            return;
        }

        GeoWarePlugin.getInstance().getPointsManager().addPoints(target, amount);
        sender.sendMessage(ChatColor.GREEN + "Se han añadido " + amount + " puntos a " + username + ". Total: " + GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(target));
    }

    private void manejarSubtract(CommandSender sender, String username, String amountStr) {
        Player target = Bukkit.getPlayerExact(username);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Jugador '" + username + "' no encontrado o no está en línea.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "La cantidad debe ser un número entero positivo.");
            return;
        }

        GeoWarePlugin.getInstance().getPointsManager().subtractPoints(target, amount);
        sender.sendMessage(ChatColor.GREEN + "Se han restado " + amount + " puntos a " + username + ". Total: " + GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(target));
    }

    private void manejarReset(CommandSender sender, String target) {
        if (target.equalsIgnoreCase("all")) {
            GeoWarePlugin.getInstance().getPointsManager().resetAllPoints();
            sender.sendMessage(ChatColor.GREEN + "Se han reiniciado los puntos de todos los jugadores.");
        } else {
            Player player = Bukkit.getPlayerExact(target);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Jugador '" + target + "' no encontrado o no está en línea.");
                return;
            }
            GeoWarePlugin.getInstance().getPointsManager().resetPlayerPoints(player);
            sender.sendMessage(ChatColor.GREEN + "Se han reiniciado los puntos de " + target + ".");
        }
    }


    private static final List<String> SUBCOMMANDS = Arrays.asList("top10", "add", "subtract", "reset");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Autocompletar subcomandos
            String input = args[0].toLowerCase();
            completions.addAll(SUBCOMMANDS.stream()
                    .filter(sub -> sub.startsWith(input))
                    .toList());
        } else if (args.length == 2) {
            // Autocompletar según el subcomando
            String subcommand = args[0].toLowerCase();
            String input = args[1].toLowerCase();

            if (subcommand.equals("add") || subcommand.equals("subtract") || subcommand.equals("reset")) {
                // Sugerir nombres de jugadores en línea
                completions.addAll(Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .toList());

                // Para "reset", también sugerir "all"
                if (subcommand.equals("reset") && "all".startsWith(input)) {
                    completions.add("all");
                }
            }
        }

        return completions;
    }
}