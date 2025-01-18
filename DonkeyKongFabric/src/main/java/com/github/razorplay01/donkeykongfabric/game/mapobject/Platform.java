package com.github.razorplay01.donkeykongfabric.game.mapobject;

import lombok.Getter;

@Getter
public class Platform extends MapObject {
    public Platform(float xPos, float yPos, float width, float height) {
        super(xPos, yPos, width, height, 0xAAcf6800);
    }
}