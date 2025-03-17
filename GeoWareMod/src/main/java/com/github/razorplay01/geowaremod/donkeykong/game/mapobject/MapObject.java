package com.github.razorplay01.geowaremod.donkeykong.game.mapobject;

import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class MapObject {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    protected final List<Hitbox> hitboxes = new ArrayList<>();
    private final GameScreen screen;

    protected MapObject(float xPos, float yPos, float width, float height, int debugColor, GameScreen screen) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.screen = screen;
        this.hitboxes.add(new RectangleHitbox("default", xPos, yPos, width, height, 0, 0, debugColor));
    }

    public void render(DrawContext context) {

    }

    public Hitbox getDefaultHitbox() {
        return hitboxes.getFirst();
    }
}