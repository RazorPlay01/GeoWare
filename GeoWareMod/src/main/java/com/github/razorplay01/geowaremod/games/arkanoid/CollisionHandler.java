package com.github.razorplay01.geowaremod.games.arkanoid;

import com.github.razorplay01.geowaremod.games.arkanoid.entity.Ball;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.Player;
import com.github.razorplay01.geowaremod.games.arkanoid.mapobject.Brick;
import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;

import java.util.Iterator;
import java.util.List;

public class CollisionHandler {
    private final ArkanoidGame game;

    public CollisionHandler(ArkanoidGame game) {
        this.game = game;
    }

    public void checkBallCollisions(Ball ball, float oldX, float oldY) {
        checkScreenCollisions(ball);
        checkBrickCollisions(ball, oldX, oldY);
        if (!game.getPlayer().isLosing()) {
            checkPlayerCollision(ball);
        }
    }

    private void checkScreenCollisions(Ball ball) {
        int screenX = game.getScreen().getGameScreenXPos();
        int screenY = game.getScreen().getGameScreenYPos();
        int screenWidth = game.getScreenWidth();
        int screenHeight = game.getScreenHeight();

        CircleHitbox circle = (CircleHitbox) ball.getDefaultHitbox();
        float radius = circle.getRadius();

        if (ball.getXPos() - radius <= screenX) {
            ball.setXPos(screenX + radius);
            ball.setDirection((float) (Math.PI - ball.getDirection()));
            ball.updateVelocities();
        } else if (ball.getXPos() + radius >= screenX + screenWidth) {
            ball.setXPos(screenX + screenWidth - radius);
            ball.setDirection((float) (Math.PI - ball.getDirection()));
            ball.updateVelocities();
        }
        if (ball.getYPos() - radius <= screenY) {
            ball.setYPos(screenY + radius);
            ball.setDirection(-ball.getDirection());
            ball.updateVelocities();
        } else if (ball.getYPos() + radius >= screenY + screenHeight) {
            ball.setActive(false);
        }
    }

    private void checkBrickCollisions(Ball ball, float oldX, float oldY) {
        Hitbox ballHitbox = ball.getDefaultHitbox();
        List<Brick> bricks = game.getBricks();
        Iterator<Brick> iterator = bricks.iterator();

        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            RectangleHitbox brickHitbox = (RectangleHitbox) brick.getDefaultHitbox();

            if (ballHitbox.intersects(brickHitbox)) {
                float collisionX = ball.getXPos() + (ball.getWidth() / 2);
                float collisionY = ball.getYPos() + (ball.getHeight() / 2);

                if (!ball.canProcessCollision(collisionX, collisionY)) {
                    continue;
                }

                boolean hitVertical = false;
                boolean hitHorizontal = false;

                CircleHitbox circle = (CircleHitbox) ballHitbox;
                float radius = circle.getRadius();

                if (oldY + ball.getHeight() <= brickHitbox.getYPos() || oldY >= brickHitbox.getYPos() + brickHitbox.getHeight()) {
                    hitVertical = true;
                }
                if (oldX + ball.getWidth() <= brickHitbox.getXPos() || oldX >= brickHitbox.getXPos() + brickHitbox.getWidth()) {
                    hitHorizontal = true;
                }

                if (hitVertical) {
                    ball.setDirection(ball.addRandomAngleVariation(-ball.getDirection()));
                    ball.setYPos(oldY);
                }
                if (hitHorizontal) {
                    ball.setDirection(ball.addRandomAngleVariation((float) (Math.PI - ball.getDirection())));
                    ball.setXPos(oldX);
                }
                if (!hitVertical && !hitHorizontal) {
                    ball.setDirection(ball.addRandomAngleVariation((float) (Math.PI + ball.getDirection())));
                }

                ball.updateVelocities();
                ball.updateLastCollisionPosition(collisionX, collisionY);

                ball.setCurrentSpeed(Math.min(ball.getCurrentSpeed() + Ball.SPEED_INCREMENT, Ball.MAX_SPEED));
                game.addScore(1, brick.getXPos() + brick.getWidth() / 2, brick.getYPos());
                iterator.remove();
                game.removeBrick(brick);
                break;
            }
        }
    }

    private void checkPlayerCollision(Ball ball) {
        Player player = game.getPlayer();
        RectangleHitbox playerDefaultHitbox = (RectangleHitbox) player.getDefaultHitbox();
        CircleHitbox ballHitbox = (CircleHitbox) ball.getDefaultHitbox();

        if (ballHitbox.intersects(playerDefaultHitbox)) {
            float radius = ballHitbox.getRadius();
            ball.setYPos(playerDefaultHitbox.getYPos() - radius * 2);

            float hitPoint = (ball.getXPos() - (playerDefaultHitbox.getXPos() + playerDefaultHitbox.getWidth() / 2))
                    / (playerDefaultHitbox.getWidth() / 2);
            hitPoint = Math.clamp(hitPoint, -1, 1);

            float baseAngle = -(float) Math.PI / 2;
            float maxAngleDeviation = (float) Math.PI / 3;
            ball.setDirection(baseAngle + (hitPoint * maxAngleDeviation));

            ball.updateVelocities();

            RectangleHitbox leftBoostHitbox = (RectangleHitbox) player.getHitboxByName("boost_ball_hitbox_l");
            RectangleHitbox rightBoostHitbox = (RectangleHitbox) player.getHitboxByName("boost_ball_hitbox_r");

            if (ballHitbox.intersects(leftBoostHitbox) || ballHitbox.intersects(rightBoostHitbox)) {
                ball.setCurrentSpeed(Math.min(ball.getCurrentSpeed() * 1.2f, Ball.MAX_SPEED));
                ball.updateVelocities();
            }

            ball.updateLastCollisionPosition(ball.getXPos() + radius, ball.getYPos() + radius);
        }
    }
}