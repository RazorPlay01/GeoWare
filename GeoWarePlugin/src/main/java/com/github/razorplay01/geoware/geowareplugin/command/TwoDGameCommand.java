package com.github.razorplay01.geoware.geowareplugin.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.razorplay01.geoware.geowareplugin.network.PacketSender;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("2dgame")
@CommandPermission("geoware.2dgame")
public class TwoDGameCommand extends BaseCommand {

    @Subcommand("tetris")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1.0|3.0|5.0")
    @Description("Inicia el juego Tetris para los jugadores objetivo")
    public void onTetris(CommandSender sender,
                         @Name("target") String target,
                         @Default("60") @Name("timeLimit") int timeLimit,
                         @Default("3.0") @Name("speed") float speed) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendTetrisPacketToClient(player, timeLimit, speed);
        }
        sender.sendMessage("§aPacket Tetris enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("hanoitowers")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 @range:1-8")
    @Description("Inicia el juego Hanoi Towers para los jugadores objetivo")
    public void onHanoiTowers(CommandSender sender,
                              @Name("target") String target,
                              @Default("60") @Name("timeLimit") int timeLimit,
                              @Default("5") @Name("rings") @Conditions("limits:min=1,max=8") int rings) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendHanoiTowersPacketToClient(player, timeLimit, rings);
        }
        sender.sendMessage("§aPacket HanoiTowers enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("donkeykong")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 800|1000|1200 0.5|0.7|0.9")
    @Description("Inicia el juego Donkey Kong para los jugadores objetivo")
    public void onDonkeyKong(CommandSender sender,
                             @Name("target") String target,
                             @Default("60") @Name("timeLimit") int timeLimit,
                             @Default("80") @Name("spawnInterval") int spawnInterval,
                             @Default("0.7") @Name("spawnProb") float spawnProb) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendDonkeyKongPacketToClient(player, timeLimit, spawnInterval, spawnProb);
        }
        sender.sendMessage("§aPacket DonkeyKong enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("bubblepuzzle")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1|2|3")
    @Description("Inicia el juego Bubble Puzzle para los jugadores objetivo")
    public void onBubblePuzzle(CommandSender sender,
                               @Name("target") String target,
                               @Default("60") @Name("timeLimit") int timeLimit,
                               @Default("1") @Name("level") @Conditions("limits:min=1,max=3") int level) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendBubblePuzzlePacketToClient(player, timeLimit, level);
        }
        sender.sendMessage("§aPacket BubblePuzzle enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("arkanoid")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1|2|3")
    @Description("Inicia el juego Arkanoid para los jugadores objetivo")
    public void onArkanoid(CommandSender sender,
                           @Name("target") String target,
                           @Default("60") @Name("timeLimit") int timeLimit,
                           @Default("1") @Name("level") @Conditions("limits:min=1,max=3") int level) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendArkanoidPacketToClient(player, timeLimit, level);
        }
        sender.sendMessage("§aPacket Arkanoid enviado a " + targets.size() + " jugador(es)");
    }
    @Subcommand("guitarhero")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator")
    @Description("Inicia el juego GuitarHero para los jugadores objetivo")
    public void onGuitarHero(CommandSender sender,
                           @Name("target") String target) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendGuitarHeroPacketToClient(player);
        }
        sender.sendMessage("§aPacket GuitarHero enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("fruitfocus")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 10 6")
    @Description("Inicia el juego Fruit Focus para los jugadores objetivo")
    public void onFruitFocus(CommandSender sender,
                             @Name("target") String target,
                             @Default("60") @Name("timeLimit") int timeLimit,
                             @Default("10") @Name("hideDuration") int hideDuration,
                             @Default("6") @Name("fruitsToHide") int fruitsToHide) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendFruitFocusPacketToClient(player, timeLimit, hideDuration, fruitsToHide);
        }
        sender.sendMessage("§aPacket FruitFocus enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("galaga")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1|2|3")
    @Description("Inicia el juego Galaga para los jugadores objetivo")
    public void onGalaga(CommandSender sender,
                         @Name("target") String target,
                         @Default("60") @Name("timeLimit") int timeLimit,
                         @Default("1") @Name("level") @Conditions("limits:min=1,max=3") int level) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendGalagaPacketToClient(player, timeLimit, level);
        }
        sender.sendMessage("§aPacket Galaga enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("keybind")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1.0|2.0|3.0 5.0|10.0")
    @Description("Inicia el juego KeyBind para los jugadores objetivo")
    public void onKeyBind(CommandSender sender,
                          @Name("target") String target,
                          @Default("60") @Name("timeLimit") int timeLimit,
                          @Default("2.0") @Name("circleSpeed") float circleSpeed,
                          @Default("0.05") @Name("spawnChance") float spawnChance) {
        if (spawnChance < 0.0f || spawnChance > 100.0f) {
            sender.sendMessage("§cLa probabilidad de spawn debe estar entre 0.0 y 100.0.");
            return;
        }
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendKeyBindPacketToClient(player, timeLimit, circleSpeed, spawnChance);
        }
        sender.sendMessage("§aPacket KeyBind enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("robotfactory")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1.0|1.5|2.0 true|false @range:1-10")
    @Description("Inicia el juego Robot Factory para los jugadores objetivo")
    public void onRobotFactory(CommandSender sender,
                               @Name("target") String target,
                               @Default("60") @Name("timeLimit") int timeLimit,
                               @Default("1.0") @Name("speed") float speed,
                               @Default("false") @Name("rotation") boolean rotation,
                               @Default("5") @Name("partQuantity") @Conditions("limits:min=1,max=10") int partQuantity) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendRobotFactoryPacketToClient(player, timeLimit, speed, rotation, partQuantity);
        }
        sender.sendMessage("§aPacket RobotFactory enviado a " + targets.size() + " jugador(es)");
    }

    @Subcommand("scarymaze")
    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator 60|180|300 1|2|3")
    @Description("Inicia el juego Scary Maze para los jugadores objetivo")
    public void onScaryMaze(CommandSender sender,
                            @Name("target") String target,
                            @Default("60") @Name("timeLimit") int timeLimit,
                            @Default("1") @Name("level") @Conditions("limits:min=1,max=3") int level) {
        Collection<Player> targets = getTargetPlayers(target);
        if (targets.isEmpty()) {
            sender.sendMessage("§cNo se encontraron jugadores válidos.");
            return;
        }
        for (Player player : targets) {
            PacketSender.sendScaryMazePacketToClient(player, timeLimit, level);
        }
        sender.sendMessage("§aPacket ScaryMaze enviado a " + targets.size() + " jugador(es)");
    }

    private Collection<Player> getTargetPlayers(String target) {
        return switch (target.toLowerCase()) {
            case "all" -> (Collection<Player>) Bukkit.getOnlinePlayers();
            case "allnotop" -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !player.isOp())
                    .collect(Collectors.toList());
            case "adventure" -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.ADVENTURE)
                    .collect(Collectors.toList());
            case "survival" -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                    .collect(Collectors.toList());
            case "spectator" -> Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getGameMode() == GameMode.SPECTATOR)
                    .collect(Collectors.toList());
            default -> {
                Player player = Bukkit.getPlayer(target);
                yield player != null && player.isOnline() ?
                        Collections.singletonList(player) :
                        Collections.emptyList();
            }
        };
    }

    @CommandCompletion("@players|all|allnotop|adventure|survival|spectator")
    @CatchUnknown
    public void onDefault(CommandSender sender) {
        sender.sendMessage("§cJuego inválido. Usa: tetris, hanoitowers, donkeykong, bubblepuzzle, arkanoid, fruitfocus, galaga, keybind, robotfactory, scarymaze");
    }
}