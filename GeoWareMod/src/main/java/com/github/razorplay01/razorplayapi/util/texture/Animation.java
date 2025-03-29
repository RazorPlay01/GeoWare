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
                currentFrame = loop ? (currentFrame + 1) % textures.size() : Math.min(currentFrame + 1, textures.size() - 1);
            }
            // No avanzar más si no hay loop y estamos en el último frame
            if (!loop && currentFrame == textures.size() - 1) {
                return; // Detener la animación aquí
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