package com.github.razorplay01.geoware.tetris;

import lombok.Getter;

@Getter
public enum BlockColor {
    YELLOW(0, 0),
    ORANGE(9, 0),
    GREEN(18, 0),
    PURPLE(27, 0),
    RED(36, 0),
    BLUE(45, 0),
    CYAN(54, 0);

    private int u;
    private int v;

    BlockColor(int u, int v) {
        this.u = u;
        this.v = v;
    }
}
