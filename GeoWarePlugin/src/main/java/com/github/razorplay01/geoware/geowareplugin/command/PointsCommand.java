package com.github.razorplay01.geoware.geowareplugin.command;

import com.github.razorplay01.geoware.geowareplugin.GeoWarePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
            sender.sendMessage(Component.text("Uso: /2dgamepoints <top10|add|subtract|reset> [opciones]").color(NamedTextColor.RED));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "top10":
                enviarTop10(sender);
                break;

            case "add":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Uso: /2dgamepoints add <username> <amount>").color(NamedTextColor.RED));
                    return true;
                }
                manejarAdd(sender, args[1], args[2]);
                break;

            case "subtract":
                if (args.length < 3) {
                    sender.sendMessage(Component.text("Uso: /2dgamepoints subtract <username> <amount>").color(NamedTextColor.RED));
                    return true;
                }
                manejarSubtract(sender, args[1], args[2]);
                break;

            case "reset":
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Uso: /2dgamepoints reset <all|username>").color(NamedTextColor.RED));
                    return true;
                }
                manejarReset(sender, args[1]);
                break;

            default:
                sender.sendMessage(Component.text("Subcomando inválido. Usa: top10, add, subtract, reset").color(NamedTextColor.RED));
                break;
        }
        return true;
    }

    private void enviarTop10(CommandSender sender) {
        sender.sendMessage(Component.text("=== Top 10 Mejores ===").color(NamedTextColor.GOLD));
        List<String> topMejores = GeoWarePlugin.getInstance().getPointsManager().getTopPlayers();
        if (topMejores.isEmpty()) {
            sender.sendMessage(Component.text("No hay jugadores con puntos aún.").color(NamedTextColor.YELLOW));
        } else {
            for (int i = 0; i < topMejores.size(); i++) {
                sender.sendMessage(Component.text("#" + (i + 1) + " " + topMejores.get(i)).color(NamedTextColor.GREEN));
            }
        }

        sender.sendMessage(Component.text("=== Top 10 Peores ===").color(NamedTextColor.GOLD));
        List<String> topPeores = GeoWarePlugin.getInstance().getPointsManager().getBottomPlayers();
        if (topPeores.isEmpty()) {
            sender.sendMessage(Component.text("No hay jugadores con puntos positivos aún.").color(NamedTextColor.YELLOW));
        } else {
            for (int i = 0; i < topPeores.size(); i++) {
                sender.sendMessage(Component.text("#" + (i + 1) + " " + topPeores.get(i)).color(NamedTextColor.RED));
            }
        }
    }

    private void manejarAdd(CommandSender sender, String username, String amountStr) {
        Player target = Bukkit.getPlayerExact(username);
        if (target == null) {
            sender.sendMessage(Component.text("Jugador '" + username + "' no encontrado o no está en línea.").color(NamedTextColor.RED));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("La cantidad debe ser un número entero positivo.").color(NamedTextColor.RED));
            return;
        }

        GeoWarePlugin.getInstance().getPointsManager().addPoints(target, amount);
        sender.sendMessage(Component.text("Se han añadido " + amount + " puntos a " + username + ". Total: " + GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(target)).color(NamedTextColor.GREEN));
    }

    private void manejarSubtract(CommandSender sender, String username, String amountStr) {
        Player target = Bukkit.getPlayerExact(username);
        if (target == null) {
            sender.sendMessage(Component.text("Jugador '" + username + "' no encontrado o no está en línea.").color(NamedTextColor.RED));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("La cantidad debe ser un número entero positivo.").color(NamedTextColor.RED));
            return;
        }

        GeoWarePlugin.getInstance().getPointsManager().subtractPoints(target, amount);
        sender.sendMessage(Component.text("Se han restado " + amount + " puntos a " + username + ". Total: " + GeoWarePlugin.getInstance().getPointsManager().getPlayerPoints(target)).color(NamedTextColor.GREEN));
    }

    private void manejarReset(CommandSender sender, String target) {
        if (target.equalsIgnoreCase("all")) {
            GeoWarePlugin.getInstance().getPointsManager().resetAllPoints();
            sender.sendMessage(Component.text("Se han reiniciado los puntos de todos los jugadores.").color(NamedTextColor.GREEN));
        } else {
            Player player = Bukkit.getPlayerExact(target);
            if (player == null) {
                sender.sendMessage(Component.text("Jugador '" + target + "' no encontrado o no está en línea.").color(NamedTextColor.RED));
                return;
            }
            GeoWarePlugin.getInstance().getPointsManager().resetPlayerPoints(player);
            sender.sendMessage(Component.text("Se han reiniciado los puntos de " + target + ".").color(NamedTextColor.GREEN));
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