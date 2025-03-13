package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;


import com.github.razorplay01.geoware.arkanoid.game.entity.Ball;
import com.github.razorplay01.geoware.arkanoid.game.stages.ArkanoidGame;
import com.github.razorplay01.geoware.arkanoid.screen.ArkanoidGameScreen;

import java.util.ArrayList;
import java.util.List;

public class MultiBallPowerUp extends PowerUp {
    public MultiBallPowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 12, 12, gameScreen, 0xFFFF0000);
    }

    @Override
    protected void onCollect() {
        ArkanoidGame game = (ArkanoidGame) gameScreen.getGame();
        List<Ball> currentBalls = new ArrayList<>(game.getBalls());

        for (Ball ball : currentBalls) {
            if (ball.isMoving()) {
                for (int i = 0; i < 2; i++) {
                    Ball newBall = new Ball(
                            ball.getXPos(),
                            ball.getYPos(),
                            ball.getWidth(),
                            ball.getHeight(),
                            gameScreen
                    );
                    newBall.start();
                    game.addBall(newBall);
                }
                break;
            }
        }
    }
}