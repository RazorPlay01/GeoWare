package com.github.razorplay01.donkeykongfabric.game.util;

import com.github.razorplay01.donkeykongfabric.game.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Animation {
    private final List<Texture> textures;
    private final float animationSpeed;

    private int currentFrame = 0;
    private float animationTick = 0;
    private boolean loop = true;

    public Animation(List<Texture> textures, float animationSpeed, boolean loop) {
        this.textures = new ArrayList<>(textures);
        this.animationSpeed = Math.max(0.1f, animationSpeed);
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
            animationTick -= animationSpeed;
            if (loop || currentFrame < textures.size() - 1) {
                currentFrame = (currentFrame + 1) % textures.size();
            }
        }

        if (!loop && currentFrame == textures.size() - 1) {
            return;
        }

        animationTick += deltaTime;
        if (animationTick >= animationSpeed) {
            animationTick -= animationSpeed;
            currentFrame = loop ? (currentFrame + 1) % textures.size() : Math.min(currentFrame + 1, textures.size() - 1);
        }
    }

    public Texture getCurrentTexture() {
        return textures.get(currentFrame);
    }

    public int getCurrentU() {
        return getCurrentTexture().u();
    }

    public int getCurrentV() {
        return getCurrentTexture().v();
    }

    public void reset() {
        this.currentFrame = 0;
        this.animationTick = 0;
    }

    public void setCurrentFrame(int frame) {
        this.currentFrame = Math.min(frame, textures.size() - 1);
        this.animationTick = 0;
    }

    public int getFrameWidth() {
        return getCurrentTexture().width();
    }

    public int getFrameHeight() {
        return getCurrentTexture().height();
    }

    public boolean isFinished() {
        return !loop && currentFrame == textures.size() - 1;
    }
}