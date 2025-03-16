package com.github.razorplay01.geowaremod.arkanoid.entity;

import lombok.Getter;

@Getter
public enum ScreenSide {
    TOP("top"),
    BOTTOM("bottom"),
    LEFT("left"),
    RIGHT("right");
    private final String side;

    ScreenSide(String side) {
        this.side = side;
    }
}
