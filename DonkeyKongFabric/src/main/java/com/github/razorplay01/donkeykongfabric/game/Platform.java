package com.github.razorplay01.donkeykongfabric.game;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

@Getter
public class Platform {
    private float x;
    private float y;
    public static final float WIDTH = 8;
    public static final float HEIGHT = 8;

    public Platform(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context) {
        context.fill(
                (int) x,
                (int) y,
                (int) (x + WIDTH),
                (int) (y + HEIGHT),
                0xAAd66b00 // Color marrón para la plataforma
        );
    }
    public boolean isTouchingBottomCollider(float x, float y) {
        float bottomColliderX = this.x;
        float bottomColliderY = this.y + HEIGHT;
        float bottomColliderWidth = WIDTH;
        float bottomColliderHeight = 2; // Altura del colisionador inferior

        return x >= bottomColliderX && x <= bottomColliderX + bottomColliderWidth &&
                y >= bottomColliderY && y <= bottomColliderY + bottomColliderHeight;
    }
    public boolean isTouchingTopCollider(float x, float y) {
        float topColliderX = this.x;
        float topColliderY = this.y - 2; // Colisionador justo encima de la plataforma
        float topColliderWidth = WIDTH;
        float topColliderHeight = 2; // Altura del colisionador superior

        return x >= topColliderX && x <= topColliderX + topColliderWidth &&
                y >= topColliderY && y <= topColliderY + topColliderHeight;
    }
}