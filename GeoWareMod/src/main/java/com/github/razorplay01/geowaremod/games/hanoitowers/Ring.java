package com.github.razorplay01.geowaremod.games.hanoitowers;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
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
    private Texture texture;

    public Ring(float xPos, float yPos, float width, float height, Texture texture) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.hitbox = new RectangleHitbox("ring", xPos, yPos, width, height, 0, 0, 0xffffffff);
    }

    public void render(DrawContext context) {
        context.drawTexture(texture.identifier(), (int) xPos, (int) yPos, texture.u(), texture.v(), texture.width(), texture.height(), texture.textureWidth(), texture.textureHeight());
    }
}
