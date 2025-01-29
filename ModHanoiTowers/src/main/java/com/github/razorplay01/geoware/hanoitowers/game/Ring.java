package com.github.razorplay01.geoware.hanoitowers.game;

import com.github.razorplay01.geoware.hanoitowers.game.util.records.Hitbox;
import com.github.razorplay01.geoware.hanoitowers.game.util.records.IHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

/**
 * Represents a ring in the game.
 * Implements the IHitbox interface to manage hitboxes for collision detection.
 */
@Getter
@Setter
public class Ring implements IHitbox {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private Hitbox hitbox;
    private int color;

    /**
     * Constructs a new Ring with the specified parameters.
     *
     * @param xPos   The x-coordinate of the ring's position.
     * @param yPos   The y-coordinate of the ring's position.
     * @param width  The width of the ring.
     * @param height The height of the ring.
     * @param color  The color of the ring.
     */
    public Ring(float xPos, float yPos, float width, float height, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.color = color;
        this.hitbox = new Hitbox("ring", xPos, yPos, width, height, 0, 0, color);
    }

    @Override
    public void updateHitboxes() {
        hitbox = new Hitbox("ring", xPos, yPos, width, height, 0, 0, color);
    }

    @Override
    public Hitbox getHitboxByName(String name) {
        if (name.equals("ring")) {
            return hitbox;
        }
        return null;
    }

    @Override
    public Hitbox getDefaultHitbox() {
        return hitbox;
    }

    /**
     * Renders the ring.
     *
     * @param context The drawing context.
     */
    public void render(DrawContext context) {
        // Draw the ring as a colored rectangle
        context.fill((int) xPos, (int) yPos, (int) (xPos + width), (int) (yPos + height), color);
    }
}
