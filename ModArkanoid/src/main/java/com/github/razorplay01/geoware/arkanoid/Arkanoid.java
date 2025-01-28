package com.github.razorplay01.geoware.arkanoid;

import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Arkanoid implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "arkanoid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean IS_DEBUG_MODE_ENABLE = false;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("arkanoid")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new GameScreen(0, 30)));
                        }
                        return 1;
                    }));
        });
    }

    @Override
    public void onInitializeClient() {
        // []
    }
}
