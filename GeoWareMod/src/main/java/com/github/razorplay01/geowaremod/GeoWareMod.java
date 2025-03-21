package com.github.razorplay01.geowaremod;

import com.github.razorplay01.geoware.geowarecommon.GeoWareCommon;
import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.bubblepuzzle.BubblePuzzleScreen;
import com.github.razorplay01.geowaremod.client.Scoreboard;
import com.github.razorplay01.geowaremod.client.ScoreboardCommand;
import com.github.razorplay01.geowaremod.donkeykong.DonkeyKongScreen;
import com.github.razorplay01.geowaremod.fruitfocus.FruitFocusGameScreen;
import com.github.razorplay01.geowaremod.galaga.GalagaScreen;
import com.github.razorplay01.geowaremod.hanoitowers.HanoiTowersScreen;
import com.github.razorplay01.geowaremod.keybind.KeyBindGameScreen;
import com.github.razorplay01.geowaremod.network.NetworkManager;
import com.github.razorplay01.geowaremod.robotfactory.RobotFactoryScreen;
import com.github.razorplay01.geowaremod.tetris.TetrisGameScreen;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
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

    @Override
    public void onInitialize() {
        GeoWareCommon.registerPackets();
        NetworkManager.register();
    }

    @Override
    public void onInitializeClient() {
        scoreboard = new Scoreboard();
        HudRenderCallback.EVENT.register(scoreboard);
        /*ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ScoreboardCommand.setScoreboard(scoreboard);
            ScoreboardCommand.register(dispatcher);
        });*/
        NetworkManager.registerClient();
        registerCommands();
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
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new BubblePuzzleScreen(0, 5, 60, 1)));
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
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new DonkeyKongScreen(0, 5, 60, 500, 0.7f)));
                        }
                        return 1;
                    }));
        });
    }
}