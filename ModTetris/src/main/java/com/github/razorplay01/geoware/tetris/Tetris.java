package com.github.razorplay01.geoware.tetris;

import com.github.razorplay01.geoware.tetris.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tetris implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "tetris";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        NetworkManager.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("tetris")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new TetrisGameScreen(0, 5, 60, 3.0f)));
                        }
                        return 1;
                    }));
        });
    }

    @Override
    public void onInitializeClient() {
        NetworkManager.registerClient();
    }
}
