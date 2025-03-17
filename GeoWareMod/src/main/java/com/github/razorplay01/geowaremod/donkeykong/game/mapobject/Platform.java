package com.github.razorplay01.geowaremod.donkeykong.game.mapobject;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import net.minecraft.client.gui.DrawContext;

import static com.github.razorplay01.geowaremod.donkeykong.game.util.TextureProvider.PLATFORM_TEXTURES;

public class Platform extends MapObject {
    private final Animation IdleAnimation = new Animation(PLATFORM_TEXTURES, 1f, false);

    public Platform(GameScreen gameScreen, float xPos, float yPos, float width, float height) {
        super(xPos, yPos, width, height, 0xAAcf6800, gameScreen);
    }

    @Override
    public void render(DrawContext context) {
        int xOffset = 0;
        int yOffset = 0;
        renderTexture(context, this, IdleAnimation, xOffset, yOffset);
        if (this.getWidth() == 16) {
            renderTexture(context, this, IdleAnimation, xOffset + 8, yOffset);
        }
        super.render(context);
    }

    public static void renderTexture(DrawContext context, MapObject entity, Animation currentAnimation, int xOffset, int yOffset) {
        Texture currentTexture = currentAnimation.getCurrentTexture();
        int width = 8;
        int height = 8;

        // Usar la posición exacta de la plataforma sin centrado adicional
        int renderX = (int) (entity.getXPos() + xOffset);
        int renderY = (int) (entity.getYPos() + yOffset);

        context.drawTexture(
                currentTexture.identifier(),
                renderX,
                renderY,
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