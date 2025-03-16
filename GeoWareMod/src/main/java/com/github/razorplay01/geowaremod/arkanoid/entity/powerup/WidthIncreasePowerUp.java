package com.github.razorplay01.geowaremod.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGameScreen;

public class WidthIncreasePowerUp extends WidthModifierPowerUp {
    private static final float WIDTH_MULTIPLIER = 2.0f;

    public WidthIncreasePowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 16, 16, gameScreen, 0xFF00FF00, WIDTH_MULTIPLIER);
    }
}