package com.github.razorplay01.geoware.hanoitowers.game;

import com.github.razorplay01.geoware.hanoitowers.game.util.records.Hitbox;
import com.github.razorplay01.geoware.hanoitowers.game.util.records.IHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tower in the game, which can hold rings.
 * Implements the IHitbox interface to manage hitboxes for collision detection.
 */
@Getter
@Setter
public class Tower implements IHitbox {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private List<Ring> rings;
    private Hitbox hitbox;
    private int color;

    /**
     * Constructs a new Tower with the specified parameters.
     *
     * @param xPos               The x-coordinate of the tower's position.
     * @param yPos               The y-coordinate of the tower's position.
     * @param width              The width of the tower.
     * @param height             The height of the tower.
     * @param color              The color of the tower.
     */
    public Tower(float xPos, float yPos, float width, float height, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.rings = new ArrayList<>();
        this.hitbox = new Hitbox("tower", xPos - 20, yPos, width + 60, height, 0, 0, color);
        this.color = color;
    }

    @Override
    public void updateHitboxes() {
        hitbox = new Hitbox("tower", xPos, yPos, width, height, 0, 0, color);
    }

    @Override
    public Hitbox getHitboxByName(String name) {
        if (name.equals("tower")) {
            return hitbox;
        }
        return null;
    }

    @Override
    public Hitbox getDefaultHitbox() {
        return hitbox;
    }

    /**
     * Adds a ring to the tower.
     *
     * @param ring The ring to add.
     */
    public void addRing(Ring ring) {
        rings.add(ring);
    }

    /**
     * Removes a ring from the tower.
     *
     * @param ring The ring to remove.
     */
    public void removeRing(Ring ring) {
        rings.remove(ring);
    }

    /**
     * Renders the tower and its rings.
     *
     * @param context The drawing context.
     */
    public void render(DrawContext context) {
        // Draw the tower as a colored rectangle
        context.fill((int) xPos, (int) yPos, (int) (xPos + width), (int) (yPos + height), color);

        // Render the rings on the tower
        for (Ring ring : rings) {
            ring.render(context);
        }
    }

    /**
     * Checks if a move with the given ring is valid.
     *
     * @param ring The ring to check.
     * @return True if the move is valid, false otherwise.
     */
    public boolean isValidMove(Ring ring) {
        if (rings.isEmpty()) {
            return true; // The tower is empty, the move is valid
        }
        Ring topRing = rings.get(rings.size() - 1);
        return ring.getWidth() < topRing.getWidth();
    }

    /**
     * Moves a ring to another tower if the move is valid.
     *
     * @param destinationTower The destination tower.
     * @param ring             The ring to move.
     * @return True if the move was successful, false otherwise.
     */
    public boolean moveRingTo(Tower destinationTower, Ring ring) {
        if (destinationTower.isValidMove(ring)) {
            this.removeRing(ring);
            // Calculate the new position of the ring in the destination tower
            ring.setXPos(destinationTower.getXPos() - (ring.getWidth() / 2) + (destinationTower.getWidth() / 2));
            ring.setYPos(destinationTower.getYPosForNewRing());
            destinationTower.addRing(ring);
            return true; // Valid move
        } else {
            // Return the ring to its original tower
            this.addRing(ring);
            return false; // Invalid move
        }
    }

    /**
     * Gets the y-coordinate for placing a new ring on the tower.
     *
     * @return The y-coordinate for the new ring.
     */
    public int getYPosForNewRing() {
        if (rings.isEmpty()) {
            return (int) (yPos + height - 20); // Ring height
        } else {
            Ring topRing = rings.get(rings.size() - 1);
            return (int) (topRing.getYPos() - 20); // Ring height
        }
    }

    /**
     * Gets the y-coordinate for re-placing a ring on the tower.
     *
     * @return The y-coordinate for re-placing the ring.
     */
    public int getYPosForRelocateRing() {
        if (rings.isEmpty() || rings.size() == 1) {
            return (int) (yPos + height - 20); // Ring height
        } else {
            Ring secondTopRing = rings.get(rings.size() - 2);
            return (int) (secondTopRing.getYPos() - 20); // Ring height
        }
    }
}
