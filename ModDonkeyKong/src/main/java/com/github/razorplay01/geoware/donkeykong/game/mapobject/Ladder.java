package com.github.razorplay01.geoware.donkeykong.game.mapobject;

import com.github.razorplay01.geoware.donkeykong.DonkeyKong;
import com.github.razorplay01.geoware.donkeykong.game.util.Animation;
import com.github.razorplay01.geoware.donkeykong.game.util.texture.Texture;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.List;

@Getter
public class Ladder extends MapObject {
    private static final Identifier EXTRA_TEXTURE = Identifier.of(DonkeyKong.MOD_ID, "textures/gui/game/extra.png");
    public final List<Texture> ladderTextures;
    private final Animation animation;
    private final boolean canPassThroughPlatform;

    public Ladder(GameScreen gameScreen, float xPos, float yPos, float width, float height, boolean canPassThroughPlatform) {
        super(gameScreen, xPos, yPos, width, height, 0xAA00ffec);
        this.canPassThroughPlatform = canPassThroughPlatform;

        int textureHeight = (int) Math.ceil((height / 8.0f) * 12);
        ladderTextures = List.of(new Texture(EXTRA_TEXTURE, 64, 58, 14, textureHeight, 79, 227, 1.0f));
        animation = new Animation(ladderTextures, 1f, false);
    }

    @Override
    public void render(DrawContext context) {
        int xOffset = 0;
        int yOffset = 0;

        // Renderizar la escalera por segmentos de 8 píxeles
        int segmentHeight = 4; // Altura de cada segmento de la escalera
        for (int segmentY = 0; segmentY < this.getHeight(); segmentY += segmentHeight) {
            // Coordenadas del segmento actual
            float segmentTopY = this.getYPos() + segmentY;
            float segmentBottomY = segmentTopY + segmentHeight;

            // Verificar si este segmento está cubierto por una plataforma
            boolean isCovered = false;
            for (Platform platform : gameScreen.getTestGame().getPlatforms()) {
                if (isOverlapping(segmentTopY, segmentBottomY, platform)) {
                    isCovered = true;
                    break;
                }
            }

            // Si no está cubierto por una plataforma, renderizar este segmento
            if (!isCovered) {
                renderTextureSegment(context, this, animation, xOffset, yOffset, segmentY, segmentHeight);
            }
        }

        super.render(context);
    }

    private boolean isOverlapping(float segmentTopY, float segmentBottomY, Platform platform) {
        // Verificar si el segmento de la escalera y la plataforma se solapan verticalmente
        float platformTopY = platform.getYPos();
        float platformBottomY = platform.getYPos() + platform.getHeight();

        // Comprobar superposición vertical
        boolean overlapsVertically = segmentBottomY > platformTopY && segmentTopY < platformBottomY;

        // Comprobar superposición horizontal (asegurar que estén en la misma posición X)
        boolean overlapsHorizontally = this.getXPos() < platform.getXPos() + platform.getWidth() &&
                this.getXPos() + this.getWidth() > platform.getXPos();

        return overlapsVertically && overlapsHorizontally;
    }

    private void renderTextureSegment(
            DrawContext context,
            MapObject entity,
            Animation currentAnimation,
            int xOffset,
            int yOffset,
            int segmentY,
            int segmentHeight) {

        // Obtener la textura actual
        Texture currentTexture = currentAnimation.getCurrentTexture();

        // Calcular dimensiones escaladas de la textura
        int width = (int) this.getWidth();
        int height = segmentHeight;

        // Calcular la posición del segmento
        int centeredX = (int) (entity.getXPos() + xOffset + (entity.getWidth() - width) / 2.0);
        int centeredY = (int) (entity.getYPos() + yOffset + segmentY);

        // Renderizar el segmento de la textura
        context.drawTexture(
                currentTexture.identifier(),
                centeredX,
                centeredY,
                width,
                height,
                currentAnimation.getCurrentU(),
                (float) currentAnimation.getCurrentV() + segmentY, // Ajuste para el segmento
                currentAnimation.getFrameWidth(),
                height,
                currentTexture.textureWidth(),
                currentTexture.textureHeight()
        );
    }
}