package com.github.razorplay01.geowaremod.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGame;
import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.arkanoid.entity.Ball;

import java.util.ArrayList;

public class MultiBallPowerUp extends PowerUp {
    public MultiBallPowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 12, 12, gameScreen, 0xFFFF0000);
    }

    @Override
    protected void onCollect(ArkanoidGame game) {
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