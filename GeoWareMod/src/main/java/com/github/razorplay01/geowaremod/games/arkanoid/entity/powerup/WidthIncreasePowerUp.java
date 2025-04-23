package com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGame;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class WidthIncreasePowerUp extends WidthModifierPowerUp {
    private static final float WIDTH_MULTIPLIER = 1.5f;

    public WidthIncreasePowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 10, 11, gameScreen, 0xFF00FF00, WIDTH_MULTIPLIER);
    }

    @Override
    public void render(DrawContext context, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/power_ups.png");
        context.drawTexture(marcoTexture, (int) xPos, (int) yPos, (int) width, (int) height, 0, 0, 20, 22, 60, 22);
    }

    @Override
    protected void onCollect(ArkanoidGame game) {
        super.onCollect(game);
        game.playSound(GameSounds.ARKANOID_UPGRADE_BAR, game.getSoundVolume(), 1.0f); // Perdió
    }
}