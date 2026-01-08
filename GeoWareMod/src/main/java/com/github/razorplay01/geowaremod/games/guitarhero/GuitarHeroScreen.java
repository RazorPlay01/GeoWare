package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.UUID;

public class GuitarHeroScreen extends GameScreen {
    public GuitarHeroScreen(int prevScore, UUID musicPlayerId) {
        super(Text.literal("Guitar Hero"));
        this.game = new GuitarHeroGame(this, prevScore, musicPlayerId);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.deltaTime = delta;
        game.renderBackground(context, mouseX, mouseY, delta);
        game.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void close() {
        GuitarHeroGame guitarHeroGame = (GuitarHeroGame) this.game;
        GeoWareMod.playingAudios.entrySet().removeIf(entry -> {
            if (entry.getValue().equals(guitarHeroGame.getMusicPlayerId())) {
                entry.getKey().release();
                return true;
            }
            return false;
        });
        super.close();
    }
}