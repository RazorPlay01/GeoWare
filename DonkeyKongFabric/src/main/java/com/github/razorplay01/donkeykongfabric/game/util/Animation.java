package com.github.razorplay01.donkeykongfabric.game.util;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;

public class Animation {
    private final int startU;
    private final int startV;
    @Getter
    private final int frameWidth;
    @Getter
    private final int frameHeight;
    private final int frameCount;
    private final float animationSpeed;
    private final boolean isVertical;

    private int currentFrame = 0;
    private float animationTick = 0;

    private boolean loop = true;

    public Animation(int startU, int startV, int frameWidth, int frameHeight, int frameCount, float animationSpeed, boolean isVertical, boolean loop) {
        this.startU = startU;
        this.startV = startV;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
        this.animationSpeed = Math.max(0.1f, animationSpeed);
        this.isVertical = isVertical;
        this.loop = loop;
    }

    public void update() {
        int fps = MinecraftClient.getInstance().getCurrentFps();
        if (fps <= 0) {
            fps = 60;
        }
        float deltaTime = 1.0f / fps;
        animationTick += deltaTime;
        if (animationTick >= animationSpeed) {
            animationTick -= animationSpeed; // Restar el tiempo excedente para mayor precisión
            if (loop || currentFrame < frameCount - 1) {
                currentFrame = (currentFrame + 1) % frameCount; // Avanzar al siguiente frame (cíclico o hasta el último frame)
            }
        }

        if (!loop && currentFrame == frameCount - 1) {
            return;
        }

        animationTick += deltaTime;
        if (animationTick >= animationSpeed) {
            animationTick -= animationSpeed;
            currentFrame = loop ? (currentFrame + 1) % frameCount : Math.min(currentFrame + 1, frameCount - 1);
        }
    }

    public int getCurrentU() {
        if (isVertical) {
            return startU;
        }
        return startU + (currentFrame * frameWidth);
    }

    public int getCurrentV() {
        if (isVertical) {
            return startV + (currentFrame * frameHeight);
        }
        return startV;
    }

    public void reset() {
        this.currentFrame = 0;
        this.animationTick = 0;
    }

    public void setCurrentFrame(int frame) {
        this.currentFrame = Math.min(frame, frameCount - 1);
        this.animationTick = 0;
    }
}