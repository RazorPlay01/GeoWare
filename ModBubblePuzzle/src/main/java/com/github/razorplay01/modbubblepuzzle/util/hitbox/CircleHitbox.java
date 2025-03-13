package com.github.razorplay01.modbubblepuzzle.util.hitbox;

import com.github.razorplay01.modbubblepuzzle.util.render.CustomDrawContext;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

@Getter
public class CircleHitbox extends Hitbox {
    private float radius;
    private static final float TOLERANCE = 1.0f;
    public CircleHitbox(String name, float xPos, float yPos, float radius, float xOffset, float yOffset, int color) {
        super(name, xPos, yPos, xOffset, yOffset, color);
        this.radius = radius;
    }

    @Override
    public Rectangle getAABB() {
        return new Rectangle((int) (xPos - radius), (int) (yPos - radius), (int) (2 * radius), (int) (2 * radius));
    }

    @Override
    public boolean intersects(Hitbox other) {
        if (!broadPhaseIntersects(other)) {
            return false; // No están cerca, no hay colisión
        }
        // Lógica de detección precisa
        if (other instanceof CircleHitbox circle) {
            float dx = getXPos() - circle.getXPos();
            float dy = getYPos() - circle.getYPos();
            float distanceSquared = dx * dx + dy * dy;
            float radiusSum = radius + circle.radius - TOLERANCE; // Restar la tolerancia
            return distanceSquared <= radiusSum * radiusSum;
        } else if (other instanceof RectangleHitbox rect) {
            float closestX = Math.clamp(getXPos(), rect.getXPos(), rect.getXPos() + rect.getWidth());
            float closestY = Math.clamp(getYPos(), rect.getYPos(), rect.getYPos() + rect.getHeight());

            float dx = getXPos() - closestX;
            float dy = getYPos() - closestY;
            return (dx * dx + dy * dy) <= (radius * radius);
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        float dx = (float) (mouseX - xPos);
        float dy = (float) (mouseY - yPos);
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    @Override
    public void draw(DrawContext drawContext) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(drawContext);
        customDrawContext.drawFilledCircle((int) xPos, (int) yPos, (int) radius, color);
    }

    public Hitbox updateRadius(float radius) {
        this.radius = radius;
        return this;
    }
}
