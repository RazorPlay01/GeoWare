package com.github.razorplay01.geowaremod.games.arkanoid.entity;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGame;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import static com.github.razorplay01.razorplayapi.util.ScreenSide.verifyScreenBoundsCollision;

@Getter
@Setter
public class Player extends ArkanoidEntity {
    public static final float MIN_WIDTH = 16f;
    public static final float MAX_WIDTH = 64f;

    private boolean isLosing;
    private boolean isWinning;
    private boolean movingLeft;
    private boolean movingRight;

    public Player(float xPos, float yPos, float width, float height, GameScreen gameScreen) {
        super(xPos, yPos, Math.clamp(width, MIN_WIDTH, MAX_WIDTH), height, gameScreen, 0xFF8300ff);
        this.speed = 2.5f;
        this.gravity = 0f;
        this.maxFallSpeed = 0f;
        this.movingLeft = false;
        this.movingRight = false;
        updateHitboxesForResize();
    }

    @Override
    public void update() {
        ArkanoidGame game = (ArkanoidGame) gameScreen.getGame();
        velocityX = 0;

        if (game.getStatus() == GameStatus.ACTIVE) {
            if (movingLeft && !movingRight) {
                velocityX = -speed;
            } else if (movingRight && !movingLeft) {
                velocityX = speed;
            }
        }

        xPos += velocityX;
        updateHitboxes();
        verifyScreenBoundsCollision(this, gameScreen, (RectangleHitbox) getDefaultHitbox());
    }

    public void moveLeft() {
        if (!isLosing) movingLeft = true;
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void moveRight() {
        if (!isLosing) movingRight = true;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void updateHitboxesForResize() {
        hitboxes.clear();
        float centerHitboxWidth = 16;
        float sideHitboxWidth = 8;
        float centerOffset = (width - centerHitboxWidth) / 2;

        hitboxes.add(new RectangleHitbox("default", xPos, yPos, width, height, 0, 0, color));
        hitboxes.add(new RectangleHitbox("ball_hitbox", xPos, yPos, centerHitboxWidth, height, centerOffset, 0, 0xFFc3c3c3));
        hitboxes.add(new RectangleHitbox("boost_ball_hitbox_l", xPos, yPos, sideHitboxWidth, height, 0, 0, 0xAAff5d00));
        hitboxes.add(new RectangleHitbox("boost_ball_hitbox_r", xPos, yPos, sideHitboxWidth, height, width - sideHitboxWidth, 0, 0xAAff5d00));
    }

    @Override
    public void setWidth(float newWidth) {
        this.width = Math.clamp(newWidth, MIN_WIDTH, MAX_WIDTH);
        updateHitboxesForResize();
    }

    public Hitbox getHitboxByName(String name) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.getHitboxId().equals(name)) return hitbox;
        }
        return null;
    }

    @Override
    public void render(DrawContext context) {
        Identifier marcoTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/player.png");
        int ancho = Math.round(width / 8);
        for (int i = 0; i < ancho - 1; i++) {
            context.drawTexture(marcoTexture, (int) xPos + i * 8, (int) yPos, 16, 8, 16, 0, 16, 16, 48, 16);
        }
        context.drawTexture(marcoTexture, (int) xPos - 8, (int) yPos, 8, 8, 0, 0, 16, 16, 48, 16);
        context.drawTexture(marcoTexture, (int) xPos + 8 * ancho, (int) yPos, 8, 8, 32, 0, 16, 16, 48, 16);
    }
}