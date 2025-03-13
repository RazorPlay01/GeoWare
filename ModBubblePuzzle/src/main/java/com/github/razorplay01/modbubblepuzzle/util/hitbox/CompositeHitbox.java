package com.github.razorplay01.modbubblepuzzle.util.hitbox;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CompositeHitbox extends Hitbox {
    private List<Hitbox> hitboxes;

    public CompositeHitbox(String hitboxId, float xPos, float yPos, float xOffset, float yOffset, int color) {
        super(hitboxId, xPos, yPos, xOffset, yOffset, color);
        this.hitboxes = new ArrayList<>();
    }

    public void addHitbox(Hitbox hitbox) {
        hitboxes.add(hitbox);
    }

    public void removeHitbox(Hitbox hitbox) {
        hitboxes.remove(hitbox);
    }

    @Override
    public Rectangle getAABB() {
        if (hitboxes.isEmpty()) {
            return new Rectangle((int) xPos, (int) yPos, 0, 0);
        }

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Hitbox hitbox : hitboxes) {
            Rectangle aabb = hitbox.getAABB();
            minX = Math.min(minX, aabb.x);
            minY = Math.min(minY, aabb.y);
            maxX = Math.max(maxX, aabb.x + aabb.width);
            maxY = Math.max(maxY, aabb.y + aabb.height);
        }

        return new Rectangle((int) minX, (int) minY, (int) (maxX - minX), (int) (maxY - minY));
    }

    @Override
    public boolean intersects(Hitbox other) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.intersects(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (Hitbox hitbox : hitboxes) {
            if (hitbox.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(DrawContext drawContext) {
        for (Hitbox hitbox : hitboxes) {
            hitbox.draw(drawContext);
        }
    }

    @Override
    public void updatePosition(float xPos, float yPos) {
        this.xPos = xPos + this.xOffset;
        this.yPos = yPos + this.yOffset;
        for (Hitbox hitbox : hitboxes) {
            hitbox.updatePosition(xPos, yPos);
        }
    }
}