package com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGame;
import com.github.razorplay01.geowaremod.games.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.ArkanoidEntity;
import net.minecraft.client.gui.DrawContext;

public abstract class PowerUp extends ArkanoidEntity {
    protected static final float FALL_SPEED = 1.0f;
    protected int animationTick = 0;
    protected boolean isActive = true; // Nueva bandera para marcar si el power-up está activo

    public PowerUp(float xPos, float yPos, float width, float height, ArkanoidGameScreen gameScreen, int color) {
        super(xPos, yPos, width, height, gameScreen, color);
        this.velocityY = FALL_SPEED;
    }

    @Override
    public void update() {
        if (!isActive) return;

        ArkanoidGame game = (ArkanoidGame) gameScreen.getGame();
        yPos += velocityY;
        updateHitboxes();

        if (yPos > game.getScreen().getGameScreenYPos() + game.getScreenHeight()) {
            isActive = false; // Marcar como inactivo en lugar de eliminar directamente
            return;
        }

        if (getDefaultHitbox().intersects(game.getPlayer().getDefaultHitbox())) {
            onCollect(game);
            isActive = false; // Marcar como inactivo tras ser recogido
        }
    }

    @Override
    public void render(DrawContext context, float delta) {

    }

    public boolean isActive() {
        return isActive;
    }

    protected abstract void onCollect(ArkanoidGame game);
}