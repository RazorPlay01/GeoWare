package com.github.razorplay01.geoware.geowarecommon.util;

public enum ScreenCoords {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,

    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,

    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    public static int getX(ScreenCoords screenCoords, int width, int scaledWidth, int xOffSet) {
        return switch (screenCoords) {
            case TOP_LEFT, BOTTOM_LEFT, CENTER_LEFT -> xOffSet;
            case TOP_RIGHT, BOTTOM_RIGHT, CENTER_RIGHT -> (width - scaledWidth) + xOffSet;
            case CENTER, TOP_CENTER, BOTTOM_CENTER -> ((width - scaledWidth) / 2) + xOffSet;
        };
    }

    public static int getY(ScreenCoords screenCoords, int height, int scaledHeight, int yOffSet) {
        return switch (screenCoords) {
            case TOP_LEFT, TOP_RIGHT, TOP_CENTER -> yOffSet;
            case BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_CENTER -> (height - scaledHeight) + yOffSet;
            case CENTER, CENTER_LEFT, CENTER_RIGHT -> ((height - scaledHeight) / 2) + yOffSet;
        };
    }
}
