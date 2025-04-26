package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
public class Note {
    public static final Identifier NOTE_TEXTURE = Identifier.of(GeoWareMod.MOD_ID, "textures/games/guitarhero/notes.png");
    private final int width;
    private final int height;
    private final int u;
    private final int v;
    private final int xPos;
    private int yPos;
    private final Key key;
    private final RectangleHitbox hitbox;
    private final float speed;
    private final long pressTime;

    public Note(int width, int height, int u, int v, int xPos, int yPos, Key key, float speed, long pressTime) {
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.xPos = xPos;
        this.yPos = yPos;
        this.key = key;
        this.hitbox = new RectangleHitbox("note", xPos, yPos, width, height, 0, 0, 0xffff0000);
        this.speed = speed;
        this.pressTime = pressTime;
    }

    public void render(DrawContext context) {
        context.drawTexture(NOTE_TEXTURE, xPos, yPos, width, height, u, v, 17, 13, 68, 13);
    }

    public void update() {
        yPos += (int) speed;
        hitbox.updatePosition(xPos, yPos);
    }

    public boolean isOutOfBounds(int screenY, int screenHeight) {
        return yPos + height > screenY + screenHeight;
    }
}