package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;


import com.github.razorplay01.geoware.arkanoid.game.entity.Ball;
import com.github.razorplay01.geoware.arkanoid.game.stages.TestGame;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;

import java.util.ArrayList;
import java.util.List;

public class MultiBallPowerUp extends PowerUp {
    public MultiBallPowerUp(float xPos, float yPos, GameScreen gameScreen) {
        super(xPos, yPos, 12, 12, gameScreen, 0xFFFF0000);
    }

    @Override
    protected void onCollect() {
        TestGame game = gameScreen.getTestGame();
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