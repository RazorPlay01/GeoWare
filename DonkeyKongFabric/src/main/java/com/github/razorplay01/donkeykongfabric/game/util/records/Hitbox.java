package com.github.razorplay01.donkeykongfabric.game.util.records;

public record Hitbox(String name, float xPos, float yPos, float width, float height, float xOffset,
                     float yOffset, int color) {
    public static final String HITBOX_DEFAULT = "default";

    public boolean intersects(Hitbox other) {
        return this.xPos < other.xPos + other.width &&
                this.xPos + this.width > other.xPos &&
                this.yPos < other.yPos + other.height &&
                this.yPos + this.height > other.yPos;
    }
}
