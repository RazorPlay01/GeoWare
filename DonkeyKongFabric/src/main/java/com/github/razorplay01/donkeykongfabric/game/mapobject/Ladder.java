package com.github.razorplay01.donkeykongfabric.game.mapobject;

import lombok.Getter;

@Getter
public class Ladder extends MapObject {
    private final boolean canPassThroughPlatform;

    public Ladder(float xPos, float yPos, float width, float height, boolean canPassThroughPlatform) {
        super(xPos, yPos, width, height,0xAA00ffec);
        this.canPassThroughPlatform = canPassThroughPlatform;
    }
}