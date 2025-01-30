package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;

import com.github.razorplay01.geoware.arkanoid.game.entity.Entity;
import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

public abstract class PowerUp extends Entity {
    protected static final float FALL_SPEED = 1.0f;
    protected int animationTick = 0;

    protected PowerUp(float xPos, float yPos, float width, float height, GameScreen gameScreen, int color) {
        super(xPos, yPos, width, height, gameScreen, color);
        this.velocityY = FALL_SPEED;
    }

    @Override
    public void update() {
        yPos += velocityY;
        updateHitboxes();

        if (yPos > gameScreen.getTestGame().getScreen().getScreenYPos() + gameScreen.getTestGame().getScreenHeight()) {
            gameScreen.getTestGame().removePowerUp(this);
            return;
        }

        Player player = gameScreen.getTestGame().getPlayer();
        if (getDefaultHitbox().intersects(player.getDefaultHitbox())) {
            onCollect();
            gameScreen.getTestGame().removePowerUp(this);
        }
    }

    @Override
    public void render(DrawContext context) {
        animationTick++;
        context.drawBorder(
                (int)xPos - 1,
                (int)yPos - 1,
                (int)width + 2,
                (int)height + 2,
                0xFFFFFFFF
        );
        context.fill(
                (int)xPos,
                (int)yPos,
                (int)(xPos + width),
                (int)(yPos + height),
                color
        );
    }

    protected abstract void onCollect();
}
