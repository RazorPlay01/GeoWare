package com.github.razorplay01.geoware.arkanoid.game.util;

public class GameTask {
    private final Runnable action;
    private int remainingTicks;

    public GameTask(Runnable action, int ticks) {
        this.action = action;
        this.remainingTicks = ticks;
    }

    public boolean update() {
        remainingTicks--;
        if (remainingTicks <= 0) {
            action.run();
            return true;
        }
        return false;
    }
}