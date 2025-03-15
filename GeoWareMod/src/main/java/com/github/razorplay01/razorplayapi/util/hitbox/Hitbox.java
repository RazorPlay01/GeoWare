package com.github.razorplay01.razorplayapi.util.hitbox;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

@Getter
public abstract class Hitbox {
    public static final String HITBOX_DEFAULT = "default";

    protected String hitboxId;
    protected float xPos;
    protected float yPos;
    protected float xOffset;
    protected float yOffset;
    protected int color;

    protected Hitbox(String hitboxId, float xPos, float yPos, float xOffset, float yOffset, int color) {
        this.hitboxId = hitboxId;
        this.xPos = xPos + xOffset;
        this.yPos = yPos + yOffset;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.color = color;
    }

    public abstract Rectangle getAABB();

    public boolean broadPhaseIntersects(Hitbox other) {
        Rectangle thisAABB = this.getAABB();
        Rectangle otherAABB = other.getAABB();
        // Verificar si las hitboxes están en la misma área general
        return Math.abs(thisAABB.x - otherAABB.x) < (thisAABB.width + otherAABB.width) &&
                Math.abs(thisAABB.y - otherAABB.y) < (thisAABB.height + otherAABB.height);
    }

    public abstract boolean intersects(Hitbox other);

    public abstract boolean isMouseOver(double mouseX, double mouseY);

    public abstract void draw(DrawContext drawContext);

    public void updatePosition(float xPos, float yPos) {
        this.xPos = xPos + this.xOffset;
        this.yPos = yPos + this.yOffset;
    }
}