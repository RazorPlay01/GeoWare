package com.github.razorplay01.geowaremod.network;

import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.packet.*;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.bubblepuzzle.BubblePuzzleScreen;
import com.github.razorplay01.geowaremod.fruitfocus.FruitFocusGameScreen;
import com.github.razorplay01.geowaremod.galaga.GalagaScreen;
import com.github.razorplay01.geowaremod.keybind.KeyBindGameScreen;
import com.github.razorplay01.geowaremod.robotfactory.RobotFactoryScreen;
import com.github.razorplay01.geowaremod.tetris.TetrisGameScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;

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
                case BubblePuzzlePacket pkt -> checkBubblePuzzlePacketClient(pkt, context);
                case FruitFocusPacket pkt -> checkFruitFocusPacketClient(pkt, context);
                case GalagaPacket pkt -> checkGalagaPacketClient(pkt, context);
                case KeyBindPacket pkt -> checkKeyBindPacketClient(pkt, context);
                case RobotFactoryPacket pkt -> checkRobotFactoryPacketClient(pkt, context);
                case TetrisPacket pkt -> checkTetrisPacketClient(pkt, context);
                default -> GeoWareMod.LOGGER.info("Packet received from server: UnknownPacket");
            }
        }));
    }

    private static void checkBubblePuzzlePacketClient(BubblePuzzlePacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new BubblePuzzleScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getLevel())));
    }

    private static void checkFruitFocusPacketClient(FruitFocusPacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new FruitFocusGameScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkGalagaPacketClient(GalagaPacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new GalagaScreen(iPacket.getLevel(), iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkKeyBindPacketClient(KeyBindPacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new KeyBindGameScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkRobotFactoryPacketClient(RobotFactoryPacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new RobotFactoryScreen(iPacket.getTimeLimitSeconds(), iPacket.getScore())));
    }

    private static void checkTetrisPacketClient(TetrisPacket iPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new TetrisGameScreen(iPacket.getScore(), 5, iPacket.getTimeLimitSeconds(), iPacket.getSpeedMultiplier())));
    }
}