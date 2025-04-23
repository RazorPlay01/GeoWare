package com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGame;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.Ball;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class MultiBallPowerUp extends PowerUp {
    public MultiBallPowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 10, 11, gameScreen, 0xFFFF0000);
    }

    @Override
    public void render(DrawContext context, float delta) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/power_ups.png");
        context.drawTexture(marcoTexture, (int) xPos, (int) yPos, (int) width, (int) height, 20, 0, 20, 22, 60, 22);
    }

    @Override
    protected void onCollect(ArkanoidGame game) {
        game.playSound(GameSounds.ARKANOID_DUPLICAR, game.getSoundVolume(), 1.0f); // Perdió
        ArrayList<Ball> currentBalls = new ArrayList<>(game.getBalls());
        for (Ball ball : currentBalls) {
            if (ball.isMoving()) {
                for (int i = 0; i < 2; i++) {
                    Ball newBall = new Ball(
                            ball.getXPos(),
                            ball.getYPos(),
                            4, // Radio, no width/height
                            gameScreen
                    );
                    newBall.setDirection(ball.getDirection() + (float) (Math.PI / 6 * (i - 0.5))); // Variar ángulo
                    newBall.start();
                    game.addBall(newBall);
                }
                break;
            }
        }
    }
}