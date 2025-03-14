package com.github.razorplay01.geoware.donkeykong.network;

import com.github.razorplay01.geoware.donkeykong.DonkeyKong;
import com.github.razorplay01.geoware.donkeykong.DonkeyKongScreen;
import com.github.razorplay01.geoware.geowarecommon.network.IPacket;
import com.github.razorplay01.geoware.geowarecommon.network.packet.DonkeyKongPacket;
import com.github.razorplay01.geoware.geowarecommon.network.packet.EmptyPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class NetworkManager {
    private static final Map<String, BiConsumer<IPacket, ClientPlayNetworking.Context>> CLIENT_HANDLERS = new HashMap<>();
    private static final Map<String, BiConsumer<IPacket, ServerPlayNetworking.Context>> SERVER_HANDLERS = new HashMap<>();

    private NetworkManager() {
        // []
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(FabricCustomPayload.CUSTOM_PAYLOAD_ID, FabricCustomPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FabricCustomPayload.CUSTOM_PAYLOAD_ID, FabricCustomPayload.CODEC);
    }

    public static void registerClient() {
        CLIENT_HANDLERS.put("DonkeyKongPacket", NetworkManager::checkArkanoidPacketClient);

        ClientPlayNetworking.registerGlobalReceiver(FabricCustomPayload.CUSTOM_PAYLOAD_ID, (payload, context) -> {
            IPacket packet = payload.packet();
            DonkeyKong.LOGGER.info("Packet received from server: {}", packet.getPacketId());
            BiConsumer<IPacket, ClientPlayNetworking.Context> handler = CLIENT_HANDLERS.getOrDefault(packet.getPacketId(), (p, c) -> DonkeyKong.LOGGER.info("Unknown server packet: {}", p.getPacketId()));
            handler.accept(packet, context);
        });
    }

    private static void checkArkanoidPacketClient(IPacket iPacket, ClientPlayNetworking.Context context) {
        DonkeyKongPacket pkt = (DonkeyKongPacket) iPacket;
        context.client().execute(() -> MinecraftClient.getInstance().setScreen(new DonkeyKongScreen(pkt.getScore(), 5, pkt.getTimeLimitSeconds(), pkt.getSpawnInterval(), pkt.getSpawnProbability())));
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(FabricCustomPayload.CUSTOM_PAYLOAD_ID, (payload, context) -> {
            IPacket packet = payload.packet();
            DonkeyKong.LOGGER.info("Packet received from client: {}", packet.getPacketId());
            BiConsumer<IPacket, ServerPlayNetworking.Context> handler = SERVER_HANDLERS.getOrDefault(packet.getPacketId(), (p, c) -> DonkeyKong.LOGGER.info("Unknown client packet: {}", p.getPacketId()));
            handler.accept(packet, context);
        });
    }
}