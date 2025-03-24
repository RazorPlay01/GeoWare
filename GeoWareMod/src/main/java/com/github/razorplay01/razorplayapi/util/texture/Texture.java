package com.github.razorplay01.razorplayapi.util.texture;

import com.github.razorplay01.razorplayapi.util.Entity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Crea una lista de texturas para animaciones a partir de una hoja de sprites.
     *
     * @param identifier    El identificador de la hoja de sprites
     * @param frameWidth    Ancho de cada frame en la hoja
     * @param frameHeight   Alto de cada frame en la hoja
     * @param textureWidth  Ancho total de la hoja de sprites
     * @param textureHeight Alto total de la hoja de sprites
     * @param frameCount    Número de frames a extraer
     * @param scale         Escala de las texturas al renderizar
     * @param horizontal    Si true, los frames se toman hacia la derecha; si false, hacia abajo
     * @return Lista de texturas generadas
     */
    public static List<Texture> createTextureList(Identifier identifier, int frameWidth, int frameHeight,
                                                  int textureWidth, int textureHeight, int frameCount,
                                                  float scale, boolean horizontal) {
        List<Texture> textures = new ArrayList<>();

        for (int i = 0; i < frameCount; i++) {
            int u, v;
            if (horizontal) {
                // Frames hacia la derecha
                u = i * frameWidth;
                v = 0;
            } else {
                // Frames hacia abajo
                u = 0;
                v = i * frameHeight;
            }

            // Verificar que no se salga de los límites de la hoja
            if (u + frameWidth <= textureWidth && v + frameHeight <= textureHeight) {
                textures.add(new Texture(identifier, u, v, frameWidth, frameHeight, textureWidth, textureHeight, scale));
            } else {
                // Si se excede, detener la creación para evitar texturas inválidas
                break;
            }
        }

        return textures;
    }
}
