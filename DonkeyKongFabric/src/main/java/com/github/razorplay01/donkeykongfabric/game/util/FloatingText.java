package com.github.razorplay01.donkeykongfabric.game.util;

import lombok.Getter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class FloatingText {
    private float x;
    private float y;
    private String text;
    private long startTime;
    private static final long DURATION = 500;
    @Getter
    private boolean isActive;
    private float scale;

    public FloatingText(float x, float y, String text, float scale) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
        this.scale = scale;
    }

    public void render(DrawContext context, TextRenderer textRenderer) {
        if (!isActive) return;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime > DURATION) {
            isActive = false;
            return;
        }

        float yOffset = (float) elapsedTime / 50;

        // Si está en el último 10% de su duración, no lo renderizamos
        if (elapsedTime > DURATION * 0.90) {
            return;
        }

        float alpha = Math.clamp(1.0f - ((float) elapsedTime / DURATION), 0.0f, 1.0f);

        int alphaInt = (int)(alpha * 255);
        int color = (alphaInt << 24) | 0xFFFFFF;

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(
                textRenderer,
                text,
                (int)(x / scale),
                (int)((y - yOffset) / scale),
                color,
                true
        );
        context.getMatrices().pop();
    }
}