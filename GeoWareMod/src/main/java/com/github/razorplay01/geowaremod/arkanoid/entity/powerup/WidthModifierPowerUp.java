package com.github.razorplay01.geowaremod.arkanoid.entity.powerup;

import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGame;
import com.github.razorplay01.geowaremod.arkanoid.ArkanoidGameScreen;
import com.github.razorplay01.geowaremod.arkanoid.entity.Player;

public abstract class WidthModifierPowerUp extends PowerUp {
    private static final int DURATION = 10000; // 10 segundos
    protected final float widthMultiplier;

    protected WidthModifierPowerUp(float xPos, float yPos, float width, float height, ArkanoidGameScreen gameScreen, int color, float widthMultiplier) {
        super(xPos, yPos, width, height, gameScreen, color);
        this.widthMultiplier = widthMultiplier;
    }

    @Override
    protected void onCollect(ArkanoidGame game) {
        Player player = game.getPlayer();
        float originalWidth = player.getWidth();
        float newWidth = Math.min(originalWidth * widthMultiplier, Player.MAX_WIDTH); // Limitar antes de calcular
        float centerX = player.getXPos() + (originalWidth / 2);

        // Solo ajustar posición si el ancho realmente cambia
        if (newWidth != originalWidth) {
            player.setXPos(centerX - (newWidth / 2));
            player.setWidth(newWidth);

            // Restaurar el tamaño original después de la duración
            game.scheduleTask(() -> {
                float currentWidth = player.getWidth();
                float endCenterX = player.getXPos() + (currentWidth / 2);
                player.setXPos(endCenterX - (originalWidth / 2));
                player.setWidth(originalWidth);
            }, DURATION);
        }
    }
}