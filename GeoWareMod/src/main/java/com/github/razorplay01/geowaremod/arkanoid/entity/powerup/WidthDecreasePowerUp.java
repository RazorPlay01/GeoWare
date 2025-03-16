package com.github.razorplay01.geowaremod.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGameScreen;

public class WidthDecreasePowerUp extends WidthModifierPowerUp {
    private static final float WIDTH_MULTIPLIER = 0.5f;

    public WidthDecreasePowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 16, 16, gameScreen, 0xFFFF0000, WIDTH_MULTIPLIER);
    }
}