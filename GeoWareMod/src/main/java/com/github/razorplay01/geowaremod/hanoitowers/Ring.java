package com.github.razorplay01.geowaremod.hanoitowers;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class Ring {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private RectangleHitbox hitbox;
    private int color;

    public Ring(float xPos, float yPos, float width, float height, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.color = color;
        this.hitbox = new RectangleHitbox("ring", xPos, yPos, width, height, 0, 0, color);
    }

    public void render(DrawContext context) {
        context.fill((int) xPos, (int) yPos, (int) (xPos + width), (int) (yPos + height), color);
    }
}
