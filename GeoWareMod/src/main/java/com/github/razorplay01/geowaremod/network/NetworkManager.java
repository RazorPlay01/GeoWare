package com.github.razorplay01.geowaremod.network;

import com.github.razorplay.packet_handler.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.packet.*;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.games.bubblepuzzle.BubblePuzzleScreen;
import com.github.razorplay01.geowaremod.games.donkeykong.DonkeyKongScreen;
import com.github.razorplay01.geowaremod.games.fruitfocus.FruitFocusGameScreen;
import com.github.razorplay01.geowaremod.games.galaga.GalagaScreen;
import com.github.razorplay01.geowaremod.games.hanoitowers.HanoiTowersScreen;
import com.github.razorplay01.geowaremod.games.keybind.KeyBindGameScreen;
import com.github.razorplay01.geowaremod.games.robotfactory.RobotFactoryScreen;
import com.github.razorplay01.geowaremod.games.scarymaze.ScaryMazeScreen;
import com.github.razorplay01.geowaremod.games.tetris.TetrisGameScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class NetworkManager {
    private NetworkManager() {
        // []
    }

    public static void register() {
        GeoWareMod.LOGGER.info("Registering Packets for " + GeoWareMod.MOD_ID);
        PayloadTypeRegistry.playC2S().register(FabricCustomPayload.CUSTOM_PAYLOAD_ID, FabricCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FabricCustomPayload.CUSTOM_PAYLOAD_ID, FabricCustomPayload.CODEC);
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(FabricCustomPayload.CUSTOM_PAYLOAD_ID, (payload, context) -> context.client().execute(() -> {
            IPacket packet = payload.packet();
            GeoWareMod.LOGGER.info("Packet received from server: {}", packet.getPacketId());
            switch (packet) {
                case ArkanoidPacket pkt -> checkArkanoidPacketClient(pkt, context);
                case BubblePuzzlePacket pkt -> checkBubblePuzzlePacketClient(pkt, context);
                case DonkeyKongPacket pkt -> checkDonkeyKongPacketClient(pkt, context);
                case FruitFocusPacket pkt -> checkFruitFocusPacketClient(pkt, context);
                case GalagaPacket pkt -> checkGalagaPacketClient(pkt, context);
                case HanoiTowersPacket pkt -> checkHanoiTowersPacketClient(pkt, context);
                case KeyBindPacket pkt -> checkKeyBindPacketClient(pkt, context);
                case RobotFactoryPacket pkt -> checkRobotFactoryPacketClient(pkt, context);
                case ScaryMazePacket pkt -> checkScaryMazePacketClient(pkt, context);
                case TetrisPacket pkt -> checkTetrisPacketClient(pkt, context);
                case ScoreboardPacket pkt -> checkScoreboardPacketClient(pkt, context);
                case ScoreUpdaterPacket pkt -> checkScoreUpdaterPacketClient(pkt, context);
                case ScoreStatusPacket pkt -> checkScoreStatusPacketClient(pkt, context);
                case EmotePacket pkt -> checkEmotePacketClient(pkt, context);
                default -> GeoWareMod.LOGGER.info("Packet received from server: UnknownPacket");
            }
        }));
    }

    private static void checkScoreStatusPacketClient(ScoreStatusPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.getScore().setVisible(iPacket.isEnable());
    }

    private static void checkScoreUpdaterPacketClient(ScoreUpdaterPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.playerScore = iPacket.getScore();
        GeoWareMod.playerPosition = iPacket.getPosition();
    }

    private static void checkEmotePacketClient(EmotePacket pkt, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if (pkt.getEmoteId().equalsIgnoreCase("\"stop\"")) {
                MinecraftClient.getInstance().player.networkHandler.sendCommand("emotes-client stop");
            } else {
                MinecraftClient.getInstance().player.networkHandler.sendCommand("emotes-client play " + pkt.getEmoteId());
            }
        });
    }

    private static void checkScoreboardPacketClient(ScoreboardPacket pkt, ClientPlayNetworking.Context context) {
        List<String> text = pkt.getTexts();
        long fadeInMs = pkt.getFadeInMs();
        long stayMs = pkt.getStayMs();
        long fadeOutMs = pkt.getFadeOutMs();
        int offsetX = pkt.getOffsetX();
        int offsetY = pkt.getOffsetY();
        float scale = pkt.getScale();
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> GeoWareMod.getScoreboard().showScoreboard(text, fadeInMs, stayMs, fadeOutMs, offsetX, offsetY, scale));
    }

    private static void checkArkanoidPacketClient(ArkanoidPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new ArkanoidGameScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getLevel())));
    }

    private static void checkBubblePuzzlePacketClient(BubblePuzzlePacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new BubblePuzzleScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getLevel())));
    }

    private static void checkDonkeyKongPacketClient(DonkeyKongPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new DonkeyKongScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getSpawnInterval(), iPacket.getSpawnProbability())));
    }

    private static void checkFruitFocusPacketClient(FruitFocusPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new FruitFocusGameScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkGalagaPacketClient(GalagaPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new GalagaScreen(iPacket.getLevel(), iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkHanoiTowersPacketClient(HanoiTowersPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new HanoiTowersScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getRings())));
    }

    private static void checkKeyBindPacketClient(KeyBindPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new KeyBindGameScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkRobotFactoryPacketClient(RobotFactoryPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new RobotFactoryScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore(), iPacket.getSpeedMultiplier(), iPacket.isEnableRotation())));
    }

    private static void checkScaryMazePacketClient(ScaryMazePacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new ScaryMazeScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getLevel())));
    }

    private static void checkTetrisPacketClient(TetrisPacket iPacket, ClientPlayNetworking.Context context) {
        GeoWareMod.guiScale = context.client().options.getGuiScale().getValue();
        context.client().options.getGuiScale().setValue(2);
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new TetrisGameScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getSpeedMultiplier())));
    }
}