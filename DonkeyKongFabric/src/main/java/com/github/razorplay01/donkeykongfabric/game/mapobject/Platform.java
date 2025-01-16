package com.github.razorplay01.donkeykongfabric.game.mapobject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Platform extends MapObject {
    public Platform(float xPos, float yPos) {
        super(xPos, yPos);
    }

    public Platform(float xPos, float yPos, float width, float height) {
        super(xPos, yPos, width, height, 0xAAd66b00);
    }
}