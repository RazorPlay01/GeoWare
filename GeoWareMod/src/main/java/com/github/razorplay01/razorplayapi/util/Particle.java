package com.github.razorplay01.razorplayapi.util;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import net.minecraft.client.gui.DrawContext;

public class Particle extends Entity {
    private final Animation particleAnimation;
    private boolean isFinished = false;

    public Particle(float xPos, float yPos, float width, float height, GameScreen gameScreen, Animation particleAnimation) {
        super(xPos, yPos, width, height, gameScreen);
        this.particleAnimation = particleAnimation;
        this.particleAnimation.reset(); // Reinicia la animación al crear la partícula
    }

    @Override
    public void update() {
        particleAnimation.update();

        if (!particleAnimation.isLoop() && particleAnimation.getCurrentFrame() == particleAnimation.getTextures().size() - 1) {
            isFinished = true;
        }
    }

    @Override
    public void render(DrawContext context) {
        renderTexture(context, this, particleAnimation, 0, 0);
    }

    public boolean isFinished() {
        return isFinished;
    }
}
