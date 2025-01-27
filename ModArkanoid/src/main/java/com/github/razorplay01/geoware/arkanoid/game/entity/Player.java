package com.github.razorplay01.geoware.arkanoid.game.entity;

import com.github.razorplay01.geoware.arkanoid.game.util.FloatingText;
import com.github.razorplay01.geoware.arkanoid.game.util.records.Hitbox;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player extends Entity {
    private boolean isLosing;
    private boolean isWinning;

    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private boolean movingLeft;
    private boolean movingRight;
    private int score = 0;

    public Player(float xPos, float yPos, float width, float height, GameScreen gameScreen) {
        super(xPos, yPos, width, height, gameScreen, 0xFF8300ff);
        this.speed = 1.5f;
        this.gravity = 0f;
        this.maxFallSpeed = 0f;

        this.movingLeft = false;
        this.movingRight = false;

        this.hitboxes.add(new Hitbox("ball_hitbox", xPos, yPos, 16, height, 8, 0, 0xFFc3c3c3));
        this.hitboxes.add(new Hitbox("boost_ball_hitbox_l", xPos, yPos, 8, height, 0, 0, 0xAAff5d00));
        this.hitboxes.add(new Hitbox("boost_ball_hitbox_r", xPos, yPos, 8, height, this.width - 8, 0, 0xAAff5d00));
    }


    public void update() {
        velocityX = 0;
        if (movingLeft && !movingRight) {
            velocityX = -speed;
        } else if (movingRight && !movingLeft) {
            velocityX = speed;
        }
        xPos += velocityX;

        updateHitboxes();
        verifyScreenBoundsCollision();
    }

    public void moveLeft() {
        if (!isLosing) {
            movingLeft = true;
        }
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void moveRight() {
        if (!isLosing) {
            movingRight = true;
        }
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    @Override
    public void render(DrawContext context) {
        renderHitboxes(context);
        floatingTexts.removeIf(text -> !text.isActive());
        for (FloatingText text : floatingTexts) {
            text.render(context, gameScreen.getTestGame().getTextRenderer());
        }
    }

    public void addScore(float xPos, float yPos, int points) {
        score += points;
        float textX = xPos + (this.getWidth() / 2);
        float textY = yPos;
        String scoreText = "" + points;
        floatingTexts.add(new FloatingText(textX, textY, scoreText, 0.5f));
    }
}
