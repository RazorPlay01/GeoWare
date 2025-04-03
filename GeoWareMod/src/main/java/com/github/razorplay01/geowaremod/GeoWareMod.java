package com.github.razorplay01.geowaremod;

import com.github.razorplay01.geoware.geowarecommon.GeoWareCommon;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.games.bubblepuzzle.BubblePuzzleScreen;
import com.github.razorplay01.geowaremod.client.Score;
import com.github.razorplay01.geowaremod.client.Scoreboard;
import com.github.razorplay01.geowaremod.games.donkeykong.DonkeyKongScreen;
import com.github.razorplay01.geowaremod.games.fruitfocus.FruitFocusGameScreen;
import com.github.razorplay01.geowaremod.games.galaga.GalagaScreen;
import com.github.razorplay01.geowaremod.games.hanoitowers.HanoiTowersScreen;
import com.github.razorplay01.geowaremod.games.keybind.KeyBindGameScreen;
import com.github.razorplay01.geowaremod.network.NetworkManager;
import com.github.razorplay01.geowaremod.games.robotfactory.RobotFactoryScreen;
import com.github.razorplay01.geowaremod.games.scarymaze.ScaryMazeScreen;
import com.github.razorplay01.geowaremod.games.tetris.TetrisGameScreen;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoWareMod implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "geowaremod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Getter
    private static Scoreboard scoreboard;
    @Getter
    private static Score score;

    public static int guiScale = 2;
    public static int playerScore = 0;
    public static int playerPosition = 0;

    @Override
    public void onInitialize() {
        GeoWareCommon.registerPackets();
        NetworkManager.register();
    }

    @Override
    public void onInitializeClient() {
        scoreboard = new Scoreboard();
        score = new Score();
        HudRenderCallback.EVENT.register(scoreboard);
        HudRenderCallback.EVENT.register(score);
        NetworkManager.registerClient();
        //registerCommands();
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("fruitfocus")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new FruitFocusGameScreen(60, 0)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("keybind")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new KeyBindGameScreen(60, 0)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("galaga1")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new GalagaScreen(1, 60 * 5, 5)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("galaga2")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new GalagaScreen(2, 60 * 5, 5)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("galaga3")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new GalagaScreen(3, 60 * 5, 5)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("robotfactory")
                    .executes(context -> {
                        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new RobotFactoryScreen(60, 0, 1.5f, true)));
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("bubblepuzzle")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new BubblePuzzleScreen(0, 5, 600, 1)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("tetris")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new TetrisGameScreen(0, 5, 60, 3.0f)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("arkanoid")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ArkanoidGameScreen(0, 5, 60, 1)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("hanoi")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new HanoiTowersScreen(0, 5, 60, 5)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("donkeykong")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new DonkeyKongScreen(0, 5, 60, 1000, 0.7f)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("maze1")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ScaryMazeScreen(0, 5, 60, 1)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("maze2")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ScaryMazeScreen(0, 5, 60, 2)));
                        }
                        return 1;
                    }));
            dispatcher.register(CommandManager.literal("maze3")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new ScaryMazeScreen(0, 5, 60, 3)));
                        }
                        return 1;
                    }));
        });
    }
}