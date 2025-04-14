package com.github.razorplay01.razorplayapi.util;

import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Entity {
    protected float xPos;
    protected float yPos;
    protected float width;
    protected float height;

    protected float velocityX = 0;
    protected float velocityY = 0;

    protected float speed = 0;

    protected GameScreen gameScreen;
    protected final List<Hitbox> hitboxes = new ArrayList<>();

    protected Entity(float xPos, float yPos, float width, float height, GameScreen gameScreen) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.gameScreen = gameScreen;
    }

    protected abstract void update();

    protected abstract void render(DrawContext context, float delta);

    public static void renderTexture(DrawContext context, Entity entity, Animation currentAnimation, int xOffset, int yOffset) {
        Texture currentTexture = currentAnimation.getCurrentTexture();

        // Calcular dimensiones escaladas de la textura
        int width = (int) (currentAnimation.getFrameWidth() * currentTexture.scale());
        int height = (int) (currentAnimation.getFrameHeight() * currentTexture.scale());

        // Calculo para centrar la textura en la entidad
        int centeredX = (int) (entity.getXPos() + xOffset + (entity.getWidth() - width) / 2.0);
        int centeredY = (int) (entity.getYPos() + yOffset + (entity.getHeight() - height) / 2.0);

        context.drawTexture(
                currentTexture.identifier(),
                centeredX,
                centeredY,
                width,
                height,
                currentAnimation.getCurrentU(),
                currentAnimation.getCurrentV(),
                currentAnimation.getFrameWidth(),
                currentAnimation.getFrameHeight(),
                currentTexture.textureWidth(),
                currentTexture.textureHeight()
        );
    }
}