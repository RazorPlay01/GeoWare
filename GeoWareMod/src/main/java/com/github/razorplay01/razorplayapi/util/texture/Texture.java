package com.github.razorplay01.razorplayapi.util.texture;

import com.github.razorplay01.razorplayapi.util.Entity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public record Texture(Identifier identifier, int u, int v, int width, int height, int textureWidth, int textureHeight,
                      float scale) {

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
