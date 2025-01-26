package com.github.razorplay01.geoware.donkeykong.game.util;

import lombok.Getter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class FloatingText {
    private float xPos;
    private float yPos;
    private String text;
    private long startTime;
    private static final long DURATION = 500;
    @Getter
    private boolean isActive;
    private float scale;

    public FloatingText(float xPos, float yPos, String text, float scale) {
        this.xPos = xPos;
        this.yPos = yPos;
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
                (int)(xPos / scale),
                (int)((yPos - yOffset) / scale),
                color,
                true
        );
        context.getMatrices().pop();
    }
}