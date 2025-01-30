package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;

import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;

public class WidthDecreasePowerUp extends PowerUp {
    private static final float WIDTH_MULTIPLIER = 0.5f;
    private static final int DURATION_TICKS = 300;

    public WidthDecreasePowerUp(float xPos, float yPos, GameScreen gameScreen) {
        super(xPos, yPos, 16, 16, gameScreen, 0xFFFF0000);
    }

    @Override
    protected void onCollect() {
        Player player = gameScreen.getTestGame().getPlayer();
        float originalWidth = player.getWidth();
        float centerX = player.getXPos() + (originalWidth / 2);

        // Aplicar nuevo ancho y actualizar hitboxes
        float newWidth = originalWidth * WIDTH_MULTIPLIER;
        player.setXPos(centerX - (newWidth / 2));
        player.setWidth(newWidth);

        // Programar la restauración
        gameScreen.getTestGame().addScheduledTask(() -> {
            float endCenterX = player.getXPos() + (newWidth / 2);
            player.setXPos(endCenterX - (originalWidth / 2));
            player.setWidth(originalWidth);
        }, DURATION_TICKS);
    }
}
