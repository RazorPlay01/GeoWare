package com.github.razorplay01.geowaremod;

import com.github.razorplay01.geoware.geowarecommon.GeoWareCommon;
import com.github.razorplay01.geowaremod.client.Score;
import com.github.razorplay01.geowaremod.client.Scoreboard;
import com.github.razorplay01.geowaremod.network.NetworkManager;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.watermedia.api.player.videolan.MusicPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeoWareMod implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "geowaremod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Getter
    private static Scoreboard scoreboard;
    @Getter
    private static Score score;

    public static int guiScale = 2;
    public static Integer playerScore = 0;
    public static Integer playerPosition = 0;

    public static final Map<MusicPlayer, UUID> playingAudios = new HashMap<>();
    public static long musicStartTime;

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
        GameSounds.registerSounds();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Gestionar MusicPlayers en cada tick
            playingAudios.entrySet().removeIf(entry -> {
                MusicPlayer player = entry.getKey();
                if (player.isEnded() || player.isBroken()) {
                    player.release();
                    LOGGER.info("MusicPlayer terminado, ID: {}", entry.getValue());
                    return true;
                }
                player.setVolume((int) (client.options.getSoundVolume(SoundCategory.MASTER) * 100));
                return false;
            });
        });
    }
}