package com.github.razorplay01.geowaremod.keybind;

import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;

public class FinalCircle extends CircleHitbox {
    public FinalCircle(float x, float y, float radius) {
        super("finalCircle", x, y, radius, 0, 0, 0xFF00FF00);
    }
}