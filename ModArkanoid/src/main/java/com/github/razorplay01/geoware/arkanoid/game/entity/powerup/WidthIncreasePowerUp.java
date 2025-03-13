package com.github.razorplay01.geoware.arkanoid.game.entity.powerup;

import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.game.stages.ArkanoidGame;
import com.github.razorplay01.geoware.arkanoid.screen.ArkanoidGameScreen;

public class WidthIncreasePowerUp extends PowerUp {
    private static final float WIDTH_MULTIPLIER = 2.0f;
    private static final int DURATION = 15000;

    public WidthIncreasePowerUp(float xPos, float yPos, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, 16, 16, gameScreen, 0xFF00FF00);
    }

    @Override
    protected void onCollect() {
        ArkanoidGame game = (ArkanoidGame) gameScreen.getGame();
        Player player = game.getPlayer();
        float originalWidth = player.getWidth();
        float centerX = player.getXPos() + (originalWidth / 2);

        // Aplicar nuevo ancho y actualizar hitboxes
        float newWidth = originalWidth * WIDTH_MULTIPLIER;
        player.setXPos(centerX - (newWidth / 2));
        player.setWidth(newWidth);

        // Programar la restauración
        game.scheduleTask(
                () -> {
                    float endCenterX = player.getXPos() + (newWidth / 2);
                    player.setXPos(endCenterX - (originalWidth / 2));
                    player.setWidth(originalWidth);
                }, DURATION);
    }
}