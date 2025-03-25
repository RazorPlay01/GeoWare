package com.github.razorplay01.geowaremod.client;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class Score implements HudRenderCallback {
    private final float scale = 1.0f;
    private final Map<Integer, Texture> numbersFontBlackOutline = new HashMap<>();
    private final Map<Integer, Texture> numbersFontWhiteOutline = new HashMap<>();

    @Getter
    @Setter
    private boolean visible = false;

    public Score() {
        Identifier numbersTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/gui/numbers.png");
        this.numbersFontBlackOutline.put(0, new Texture(numbersTexture, 0, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(1, new Texture(numbersTexture, 6, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(2, new Texture(numbersTexture, 12, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(3, new Texture(numbersTexture, 18, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(4, new Texture(numbersTexture, 24, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(5, new Texture(numbersTexture, 30, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(6, new Texture(numbersTexture, 36, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(7, new Texture(numbersTexture, 42, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(8, new Texture(numbersTexture, 48, 0, 6, 10, 60, 20, scale));
        this.numbersFontBlackOutline.put(9, new Texture(numbersTexture, 54, 0, 6, 10, 60, 20, scale));

        this.numbersFontWhiteOutline.put(0, new Texture(numbersTexture, 0, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(1, new Texture(numbersTexture, 6, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(2, new Texture(numbersTexture, 12, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(3, new Texture(numbersTexture, 18, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(4, new Texture(numbersTexture, 24, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(5, new Texture(numbersTexture, 30, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(6, new Texture(numbersTexture, 36, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(7, new Texture(numbersTexture, 42, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(8, new Texture(numbersTexture, 48, 10, 6, 10, 60, 20, scale));
        this.numbersFontWhiteOutline.put(9, new Texture(numbersTexture, 54, 10, 6, 10, 60, 20, scale));
    }

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (visible) {
            int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            Identifier numbersTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/gui/score.png");
            int x = (int) (windowWidth - (172 * scale));
            int y = (int) (windowHeight - (16 * scale));
            drawContext.drawTexture(numbersTexture, x, y, (int) (172 * scale), (int) (16 * scale), 0, 0, 172, 16, 172, 16);

            // Manejo de la puntuación (máximo 999)
            int score = Math.min(GeoWareMod.playerScore, 999);
            String scoreStr = String.format("%03d", score); // Siempre 3 dígitos
            drawNumber(drawContext, scoreStr, numbersFontBlackOutline, x + 19 * scale, y + 3 * scale, 16 * scale);

            // Manejo de la posición (máximo 99)
            int position = Math.min(GeoWareMod.playerPosition, 99);
            String positionStr = String.format("%02d", position); // Siempre 2 dígitos
            drawNumber(drawContext, positionStr, numbersFontWhiteOutline, x + 126 * scale, y + 3 * scale, 16 * scale);
        }
    }

    private void drawNumber(DrawContext drawContext, String numberStr, Map<Integer, Texture> font, float x, float y, float spacing) {
        for (int i = 0; i < numberStr.length(); i++) {
            int digit = Character.getNumericValue(numberStr.charAt(i));
            Texture drawTexture = font.get(digit);
            int width = (int) (drawTexture.width() * drawTexture.scale());
            int height = (int) (drawTexture.height() * drawTexture.scale());
            drawContext.drawTexture(
                    drawTexture.identifier(),
                    (int) (x + i * spacing),
                    (int) y,
                    width,
                    height,
                    drawTexture.u(),
                    drawTexture.v(),
                    drawTexture.width(),
                    drawTexture.height(),
                    drawTexture.textureWidth(),
                    drawTexture.textureHeight()
            );
        }
    }
}