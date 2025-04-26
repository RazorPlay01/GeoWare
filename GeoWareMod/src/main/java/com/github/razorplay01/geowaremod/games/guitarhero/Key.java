package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class Key {
    public static final Identifier KEY_TEXTURE = Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/botones.png");
    private final int width;
    private final int height;
    private final int u;
    private final int v;
    private final int xPos;
    private final int yPos;
    private final RectangleHitbox hitbox;

    private boolean isPressed;

    public Key(int width, int height, int u, int v, int xPos, int yPos, RectangleHitbox hitbox) {
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.xPos = xPos;
        this.yPos = yPos;
        this.hitbox = hitbox;
        this.isPressed = false;
    }

    public void render(DrawContext context) {
        if (isPressed) {
            context.drawTexture(KEY_TEXTURE, xPos, yPos, width, height, u, v + 16, 19, 16, 76, 32);
        } else {
            context.drawTexture(KEY_TEXTURE, xPos, yPos, width, height, u, v, 19, 16, 76, 32);
        }
    }
}