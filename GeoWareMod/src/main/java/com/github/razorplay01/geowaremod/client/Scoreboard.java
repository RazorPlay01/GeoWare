package com.github.razorplay01.geowaremod.client;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.List;

public class Scoreboard implements HudRenderCallback {
    private float scale = 1.0f;
    private List<String> textList;
    private static final Identifier SCOREBOARD_TEXTURE = Identifier.of(GeoWareMod.MOD_ID, "textures/gui/scoreboard.png");
    private static final int[] TEXT_POS_Y = {27, 39, 51, 63, 74, 86, 98, 110, 122, 134, 146, 158};

    // Variables de tiempo
    private long startTime = 0;
    private long fadeInDuration = 0;
    private long stayDuration = 0;
    private long fadeOutDuration = 0;
    private long totalDuration = 0;

    // Offsets
    private int offsetX = 0;
    private int offsetY = 0;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        long currentTime = System.currentTimeMillis();
        if (currentTime <= startTime + totalDuration && startTime > 0) {
            renderScoreboard(drawContext);
        }
    }

    // Método público actualizado con nuevos parámetros
    public void showScoreboard(List<String> texts, long fadeInMs, long stayMs, long fadeOutMs, int offsetX, int offsetY, float scale) {
        this.textList = texts;
        this.fadeInDuration = fadeInMs;
        this.stayDuration = stayMs;
        this.fadeOutDuration = fadeOutMs;
        this.totalDuration = fadeInMs + stayMs + fadeOutMs;
        this.startTime = System.currentTimeMillis();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
    }

    private void renderScoreboard(DrawContext context) {
        if (textList == null || textList.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Calcular dimensiones escaladas
        int textureWidth = (int) (96 * scale);
        int textureHeight = (int) (171 * scale);

        // Posición base (centrado en Y)
        int baseX = screenWidth - textureWidth - 10;
        int baseY = (screenHeight - textureHeight) / 2;

        // Aplicar offsets
        int finalBaseX = baseX + offsetX;
        int finalBaseY = baseY + offsetY;

        // Calcular animación
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        float animationProgress = 0;
        float alpha = 1.0f;

        if (elapsedTime < fadeInDuration) {
            // Fade In: desde fuera de la pantalla hacia la posición final
            animationProgress = (float) elapsedTime / fadeInDuration;
            alpha = animationProgress;
        } else if (elapsedTime < fadeInDuration + stayDuration) {
            // Stay: posición final
            animationProgress = 1.0f;
            alpha = 1.0f;
        } else if (elapsedTime < totalDuration) {
            // Fade Out: desde posición final hacia fuera
            animationProgress = 1.0f - ((float) (elapsedTime - fadeInDuration - stayDuration) / fadeOutDuration);
            alpha = animationProgress;
        }

        // Calcular posición X animada (entra desde la derecha)
        int animatedX = (int) (screenWidth + (finalBaseX - screenWidth) * animationProgress);
        int animatedY = finalBaseY;

        // Configurar renderizado con transparencia
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        // Renderizar la textura
        context.drawTexture(SCOREBOARD_TEXTURE, animatedX, animatedY, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        // Renderizar textos
        for (int i = 0; i < Math.min(12, textList.size()); i++) {
            String text = textList.get(i);

            // Calcular posición escalada del texto
            int textX = (int) (animatedX + (22 * scale));
            int textY = (int) (animatedY + (TEXT_POS_Y[i] * scale));

            // Calcular escala del texto
            float textScale = scale;
            int maxWidth = (int) (52 * scale);
            int textWidth = client.textRenderer.getWidth(text);

            if (textWidth > maxWidth) {
                textScale = (float) maxWidth / textWidth;
            }

            // Aplicar escala y transparencia al texto
            context.getMatrices().push();
            context.getMatrices().translate(textX, textY, 0);
            context.getMatrices().scale(textScale, textScale, 1.0f);

            // Dibujar texto con sombra
            context.drawText(client.textRenderer, text, 0, 0, 0xFFFFFF | ((int) (alpha * 255) << 24), true);

            context.getMatrices().pop();
        }

        // Restaurar estado
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}