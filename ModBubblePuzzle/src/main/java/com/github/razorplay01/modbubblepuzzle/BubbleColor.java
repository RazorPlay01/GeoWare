package com.github.razorplay01.modbubblepuzzle;

import lombok.Getter;

@Getter
public enum BubbleColor {
    YELLOW(0xFFFFFF00),
    RED(0xFFFF0000),
    BLUE(0xFF0000FF),
    GREEN(0xFF00FF00);
    private final int color;

    BubbleColor(int color) {
        this.color = color;
    }
}
