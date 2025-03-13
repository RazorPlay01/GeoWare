package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;

import com.github.razorplay01.geoware.arkanoid.game.entity.Entity;
import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.game.stages.ArkanoidGame;
import com.github.razorplay01.geoware.arkanoid.screen.ArkanoidGameScreen;
import net.minecraft.client.gui.DrawContext;

public abstract class PowerUp extends Entity {
    protected static final float FALL_SPEED = 1.0f;
    protected int animationTick = 0;

    protected PowerUp(float xPos, float yPos, float width, float height, ArkanoidGameScreen gameScreen, int color) {
        super(xPos, yPos, width, height, gameScreen, color);
        this.velocityY = FALL_SPEED;
    }

    @Override
    public void update() {
        ArkanoidGame game = (ArkanoidGame) gameScreen.getGame();
        yPos += velocityY;
        updateHitboxes();

        if (yPos > gameScreen.getGame().getScreen().getGameScreenYPos() + gameScreen.getGame().getScreenHeight()) {
            game.removePowerUp(this);
            return;
        }

        Player player = game.getPlayer();
        if (getDefaultHitbox().intersects(player.getDefaultHitbox())) {
            onCollect();
            game.removePowerUp(this);
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
