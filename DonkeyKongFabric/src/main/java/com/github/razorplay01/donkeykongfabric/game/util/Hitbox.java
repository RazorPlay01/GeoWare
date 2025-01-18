package com.github.razorplay01.donkeykongfabric.game.util;

public record Hitbox(String name, float x, float y, float width, float height, float xOffset,
                     float yOffset, int color) {
    public boolean intersects(Hitbox other) {
        return this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y;
    }
}
