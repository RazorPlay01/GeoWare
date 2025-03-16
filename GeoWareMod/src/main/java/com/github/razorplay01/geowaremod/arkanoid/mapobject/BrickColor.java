package com.github.razorplay01.geowaremod.arkanoid.mapobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrickColor {
    RED(0xFFFF0000),
    GREEN(0xFF00FF00),
    BLUE(0xFF0000FF),
    YELLOW(0xFFFFFF00),
    MAGENTA(0xFFFF00FF),
    GRAY(0xFFa0a0a0),
    CYAN(0xFF00FFFF);

    private final int color;
}
