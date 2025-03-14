package com.github.razorplay01.modbubblepuzzle;

import com.github.razorplay01.modbubblepuzzle.network.NetworkManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModBubblePuzzle implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "bubblepuzzle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean IS_DEBUG_MODE_ENABLE = false;

    @Override
    public void onInitialize() {
        NetworkManager.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("bubblepuzzle")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new BubblePuzzleScreen(0, 5, 60, 1)));
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
