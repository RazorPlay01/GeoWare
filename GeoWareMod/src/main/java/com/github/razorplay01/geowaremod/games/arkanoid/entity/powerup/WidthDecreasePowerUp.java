package com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class WidthDecreasePowerUp extends WidthModifierPowerUp {
    private static final float WIDTH_MULTIPLIER = 0.5f;

    public WidthDecreasePowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 10, 11, gameScreen, 0xFFFF0000, WIDTH_MULTIPLIER);
    }

    @Override
    public void render(DrawContext context) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/power_ups.png");
        context.drawTexture(marcoTexture, (int) xPos, (int) yPos, (int) width, (int) height, 40, 0, 20, 22, 60, 22);
    }
}