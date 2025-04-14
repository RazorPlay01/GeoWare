package com.github.razorplay01.razorplayapi.util.texture;

import com.github.razorplay01.razorplayapi.util.Entity;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

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
        reset();
    }

    public void update(float delta) {
        animationTick += delta;

        // Calcular cuántos frames avanzar basado en el tiempo acumulado
        if (animationTick >= animationSpeed) {
            int framesToAdvance = (int) (animationTick / animationSpeed);
            animationTick -= framesToAdvance * animationSpeed;

            if (loop || currentFrame < textures.size() - 1) {
                currentFrame += framesToAdvance;
                if (loop) {
                    currentFrame %= textures.size();
                } else {
                    currentFrame = Math.min(currentFrame, textures.size() - 1);
                }
            }
        }
    }

    public Texture getCurrentTexture() {
        return textures.get(currentFrame);
    }

    public float getCurrentU() {
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
        boolean finished = !loop && currentFrame == textures.size() - 1;
        return finished;
    }

    public void renderAnimation(DrawContext context, int xOffset, int yOffset, int xPos, int yPos, int width, int height) {
        Texture currentTexture = this.getCurrentTexture();

        // Calcular dimensiones escaladas de la textura
        int tWidth = (int) (this.getFrameWidth() * currentTexture.scale());
        int tHeight = (int) (this.getFrameHeight() * currentTexture.scale());

        // Calculo para centrar la textura en la entidad
        int centeredX = (int) (xPos + xOffset + (width - tWidth) / 2.0);
        int centeredY = (int) (yPos + yOffset + (height - tHeight) / 2.0);

        context.drawTexture(
                currentTexture.identifier(),
                centeredX,
                centeredY,
                width,
                height,
                this.getCurrentU(),
                this.getCurrentV(),
                this.getFrameWidth(),
                this.getFrameHeight(),
                currentTexture.textureWidth(),
                currentTexture.textureHeight()
        );
    }
    public void renderAnimation(DrawContext context, int xPos, int yPos, int width, int height) {
        Texture currentTexture = this.getCurrentTexture();

        // Calcular dimensiones escaladas de la textura
        int tWidth = (int) (this.getFrameWidth() * currentTexture.scale());
        int tHeight = (int) (this.getFrameHeight() * currentTexture.scale());

        // Calculo para centrar la textura en la entidad
        int centeredX = (int) (xPos + (width - tWidth) / 2.0);
        int centeredY = (int) (yPos + (height - tHeight) / 2.0);

        context.drawTexture(
                currentTexture.identifier(),
                centeredX,
                centeredY,
                width,
                height,
                this.getCurrentU(),
                this.getCurrentV(),
                this.getFrameWidth(),
                this.getFrameHeight(),
                currentTexture.textureWidth(),
                currentTexture.textureHeight()
        );
    }
}