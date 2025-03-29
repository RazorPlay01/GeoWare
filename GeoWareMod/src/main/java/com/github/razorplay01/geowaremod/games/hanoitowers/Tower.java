package com.github.razorplay01.geowaremod.games.hanoitowers;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Tower {
    private float xPos;
    private float yPos;
    private float width;
    private float height;
    private List<Ring> rings;
    private RectangleHitbox hitbox;
    private int color;

    public Tower(float xPos, float yPos, float width, float height, int color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.rings = new ArrayList<>();
        this.hitbox = new RectangleHitbox("tower", xPos - 20, yPos, width + 60, height, 0, 0, color);
        this.color = color;
    }

    public void addRing(Ring ring) {
        rings.add(ring);
    }

    public void removeRing(Ring ring) {
        rings.remove(ring);
    }

    public void render(DrawContext context) {
        for (Ring ring : rings) {
            ring.render(context);
        }
    }

    public boolean isValidMove(Ring ring) {
        if (rings.isEmpty()) {
            return true;
        }
        Ring topRing = rings.get(rings.size() - 1);
        return ring.getWidth() < topRing.getWidth();
    }

    public void moveRingTo(Tower destinationTower, Ring ring) {
        if (destinationTower.isValidMove(ring)) {
            this.removeRing(ring);
            ring.setXPos(destinationTower.getXPos() - (ring.getWidth() / 2) + (destinationTower.getWidth() / 2));
            ring.setYPos(destinationTower.getYPosForNewRing());
            destinationTower.addRing(ring);
        } else {
            this.addRing(ring);
        }
    }

    public int getYPosForNewRing() {
        if (rings.isEmpty()) {
            return (int) (yPos + height - 10 - 6); // Ring height
        } else {
            Ring topRing = rings.get(rings.size() - 1);
            return (int) (topRing.getYPos() - 10); // Ring height
        }
    }

    public int getYPosForRelocateRing() {
        if (rings.isEmpty() || rings.size() == 1) {
            return (int) (yPos + height - 10 - 6); // Ring height
        } else {
            Ring secondTopRing = rings.get(rings.size() - 2);
            return (int) (secondTopRing.getYPos() - 10); // Ring height
        }
    }
}
