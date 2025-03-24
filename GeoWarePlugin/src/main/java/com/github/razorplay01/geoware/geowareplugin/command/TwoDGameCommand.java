package com.github.razorplay01.geoware.geowareplugin.command;

import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TwoDGameCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION = "geoware.2dgame";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar permiso
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUso: /2dgame <target> <game> [parametros]");
            return true;
        }

        String target = args[0];
        String game = args[1].toLowerCase();
        Collection<Player> targets = getTargetPlayers(target);

        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return true;
        }

        try {
            switch (game) {
                case "tetris":
                    int tetrisTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    float tetrisSpeed = args.length > 3 ? Float.parseFloat(args[3]) : 3.0f;
                    for (Player player : targets) {
                        PacketSender.sendTetrisPacketToClient(player, tetrisTime, tetrisSpeed);
                    }
                    sender.sendMessage("§aPacket Tetris enviado a " + targets.size() + " jugador(es)");
                    break;

                case "hanoitowers":
                    int hanoiTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int rings = args.length > 3 ? Integer.parseInt(args[3]) : 5;
                    for (Player player : targets) {
                        PacketSender.sendHanoiTowersPacketToClient(player, hanoiTime, rings);
                    }
                    sender.sendMessage("§aPacket HanoiTowers enviado a " + targets.size() + " jugador(es)");
                    break;

                case "donkeykong":
                    int dkTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int spawnInterval = args.length > 3 ? Integer.parseInt(args[3]) : 80;
                    float spawnProb = args.length > 4 ? Float.parseFloat(args[4]) : 0.7f;
                    for (Player player : targets) {
                        PacketSender.sendDonkeyKongPacketToClient(player, dkTime, spawnInterval, spawnProb);
                    }
                    sender.sendMessage("§aPacket DonkeyKong enviado a " + targets.size() + " jugador(es)");
                    break;

                case "bubblepuzzle":
                    int bubbleTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int bubbleLevel = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                    for (Player player : targets) {
                        PacketSender.sendBubblePuzzlePacketToClient(player, bubbleTime, bubbleLevel);
                    }
                    sender.sendMessage("§aPacket BubblePuzzle enviado a " + targets.size() + " jugador(es)");
                    break;

                case "arkanoid":
                    int arkanoidTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int arkanoidLevel = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                    for (Player player : targets) {
                        PacketSender.sendArkanoidPacketToClient(player, arkanoidTime, arkanoidLevel);
                    }
                    sender.sendMessage("§aPacket Arkanoid enviado a " + targets.size() + " jugador(es)");
                    break;

                case "fruitfocus":
                    int fruitTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    for (Player player : targets) {
                        PacketSender.sendFruitFocusPacketToClient(player, fruitTime);
                    }
                    sender.sendMessage("§aPacket FruitFocus enviado a " + targets.size() + " jugador(es)");
                    break;

                case "galaga":
                    int galagaTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int galagaLevel = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                    for (Player player : targets) {
                        PacketSender.sendGalagaPacketToClient(player, galagaTime, galagaLevel);
                    }
                    sender.sendMessage("§aPacket Galaga enviado a " + targets.size() + " jugador(es)");
                    break;

                case "keybind":
                    int keyTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    for (Player player : targets) {
                        PacketSender.sendKeyBindPacketToClient(player, keyTime);
                    }
                    sender.sendMessage("§aPacket KeyBind enviado a " + targets.size() + " jugador(es)");
                    break;

                case "robotfactory":
                    int robotTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    float robotSpeed = args.length > 3 ? Float.parseFloat(args[3]) : 1.0f;
                    boolean enableRotation = args.length > 4 ? Boolean.parseBoolean(args[4]) : false;
                    for (Player player : targets) {
                        PacketSender.sendRobotFactoryPacketToClient(player, robotTime, robotSpeed, enableRotation);
                    }
                    sender.sendMessage("§aPacket RobotFactory enviado a " + targets.size() + " jugador(es)");
                    break;

                case "scarymaze":
                    int scaryMazeTime = args.length > 2 ? Integer.parseInt(args[2]) : 60;
                    int scaryMazeLevel = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                    for (Player player : targets) {
                        PacketSender.sendScaryMazePacketToClient(player, scaryMazeTime, scaryMazeLevel);
                    }
                    sender.sendMessage("§aPacket ScaryMaze enviado a " + targets.size() + " jugador(es)");
                    break;

                default:
                    sender.sendMessage("§cJuego inválido. Usa: tetris, hanoitowers, donkeykong, bubblepuzzle, arkanoid, fruitfocus, galaga, keybind o robotfactory");
                    return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cError: Los parámetros numéricos no son válidos");
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Verificar permiso para tab completion
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
        } else if (args.length == 2) {
            // Completar juegos
            completions.add("tetris");
            completions.add("hanoitowers");
            completions.add("donkeykong");
            completions.add("bubblepuzzle");
            completions.add("arkanoid");
            completions.add("fruitfocus");
            completions.add("galaga");
            completions.add("keybind");
            completions.add("robotfactory");
            completions.add("scarymaze");
        } else if (args.length == 3) {
            // Completar timeLimit
            completions.add("60");
            completions.add("180");
            completions.add("300");
        } else if (args.length == 4) {
            switch (args[1].toLowerCase()) {
                case "tetris":
                    completions.add("3.0");
                    break;
                case "hanoitowers":
                    completions.add("1");
                    completions.add("2");
                    completions.add("3");
                    completions.add("4");
                    completions.add("5");
                    completions.add("6");
                    completions.add("7");
                    completions.add("8");
                    break;
                case "donkeykong":
                    completions.add("500");
                    break;
                case "bubblepuzzle":
                case "arkanoid":
                case "galaga":
                case "scarymaze":
                    completions.add("1");
                    completions.add("2");
                    completions.add("3");
                    break;
                case "robotfactory":
                    completions.add("1.0");
                    completions.add("1.5");
                    completions.add("2.0");
                    break;
            }
        } else if (args.length == 5) {
            switch (args[1].toLowerCase()) {
                case "donkeykong":
                    completions.add("0.7");
                    break;
                case "robotfactory":
                    completions.add("true");
                    completions.add("false");
                    break;
            }
        }

        return completions.stream()
                .filter(c -> c.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
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