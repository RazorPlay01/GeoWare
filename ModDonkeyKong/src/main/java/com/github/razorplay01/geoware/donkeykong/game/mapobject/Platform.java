package com.github.razorplay01.geoware.donkeykong.game.mapobject;

import com.github.razorplay01.geoware.donkeykong.game.util.Animation;
import com.github.razorplay01.geoware.donkeykong.game.util.texture.Texture;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

import static com.github.razorplay01.geoware.donkeykong.game.util.texture.TextureProvider.PLATFORM_TEXTURES;

public class Platform extends MapObject {
    private final Animation IdleAnimation = new Animation(PLATFORM_TEXTURES, 1f, false);

    public Platform(GameScreen gameScreen, float xPos, float yPos, float width, float height) {
        super(gameScreen, xPos, yPos, width, height, 0xAAcf6800);
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

        // Calcular dimensiones escaladas de la textura
        int width = 8;
        int height = 8;

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