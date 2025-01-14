package com.github.razorplay01.donkeykongfabric.game;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

@Getter
public class Ladder {
    private float x;
    private float y;
    private final float width = 8;
    private final float height; // Ahora la altura será variable
    private final boolean canPassThroughPlatform; // Nuevo atributo

    public Ladder(float x, float y, float height, boolean canPassThroughPlatform) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.canPassThroughPlatform = canPassThroughPlatform;
    }

    public boolean isWithinBounds(Barrel barrel) {
        return barrel.getX() + barrel.getWidth() > x &&
                barrel.getX() < x + width &&
                barrel.getY() + barrel.getHeight() > y &&
                barrel.getY() < y + height;
    }

    public boolean canUseLadder(Barrel barrel) {
        float barrelCenterX = barrel.getX() + (barrel.getWidth() / 2);
        float ladderCenterX = x + (width / 2);
        float allowedOffset = 8f; // Aumentamos el rango de detección para los barriles

        // Verificar si el barril está en el rango horizontal de la escalera
        boolean isAlignedHorizontally = Math.abs(barrelCenterX - ladderCenterX) <= allowedOffset;

        // Verificar si el barril está en el rango vertical de la escalera
        boolean isAtTopOfLadder = barrel.getY() + barrel.getHeight() >= y &&
                barrel.getY() + barrel.getHeight() <= y + 8;

        return isAlignedHorizontally && isAtTopOfLadder;
    }

    public void render(DrawContext context) {
        // Dibujar la escalera completa
        context.fill(
                (int) x,
                (int) y,
                (int) (x + width),
                (int) (y + height),
                0xAAFFFF00
        );
    }

    public static final float LADDER_USE_THRESHOLD = 6f; // Umbral para usar la escalera

    public boolean canUseLadder(Player player) {
        float playerCenterX = player.getXPos() + Player.PLAYER_WIDTH / 2;
        float playerBottomCenterY = player.getYPos() + Player.PLAYER_HEIGHT;

        boolean horizontalAlign = Math.abs(playerCenterX - (x + width / 2)) < LADDER_USE_THRESHOLD;
        boolean verticalAlign = playerBottomCenterY >= y && playerBottomCenterY <= y + height;

        return horizontalAlign && verticalAlign;
    }
}