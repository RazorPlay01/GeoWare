package com.github.razorplay01.modbubblepuzzle.util.hitbox;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

@Getter
public class RectangleHitbox extends Hitbox {
    private float width;
    private float height;

    public RectangleHitbox(String name, float xPos, float yPos, float width, float height, float xOffset, float yOffset, int color) {
        super(name, xPos, yPos, xOffset, yOffset, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public Rectangle getAABB() {
        return new Rectangle((int) xPos, (int) yPos, (int) width, (int) height);
    }

    @Override
    public boolean intersects(Hitbox other) {
        if (!broadPhaseIntersects(other)) {
            return false; // No están cerca, no hay colisión
        }
        // Lógica de detección precisa
        if (other instanceof RectangleHitbox rect) {
            return this.xPos < rect.xPos + rect.width &&
                    this.xPos + this.width > rect.xPos &&
                    this.yPos < rect.yPos + rect.height &&
                    this.yPos + this.height > rect.yPos;
        } else if (other instanceof CircleHitbox) {
            return other.intersects(this);
        }
        return false;
    }


    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= xPos && mouseX <= xPos + width &&
                mouseY >= yPos && mouseY <= yPos + height;
    }

    @Override
    public void draw(DrawContext drawContext) {
        drawContext.fill((int) xPos, (int) yPos, (int) (xPos + width), (int) (yPos + height), color);
    }

    public Hitbox updateSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }
}