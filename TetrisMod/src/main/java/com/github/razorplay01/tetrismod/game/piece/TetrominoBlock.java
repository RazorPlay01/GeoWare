package com.github.razorplay01.tetrismod.game.piece;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class TetrominoBlock implements Cloneable {
    private int x;
    private int y;
    private int color;

    public TetrominoBlock(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public TetrominoBlock clone() {
        try {
            return (TetrominoBlock) super.clone();
        } catch (CloneNotSupportedException e) {
            return new TetrominoBlock(this.x, this.y, this.color);
        }
    }

    public void render(DrawContext context, int offsetX, int offsetY, int size) {
        // Dibujar el bloque principal
        context.fill(
                offsetX,
                offsetY,
                offsetX + size,
                offsetY + size,
                this.color
        );

        // Calcular color más oscuro para el borde
        int borderColor = darkenColor(this.color);

        // Dibujar los bordes
        // Borde superior
        context.fill(
                offsetX,
                offsetY,
                offsetX + size,
                offsetY + 1,
                borderColor
        );
        // Borde inferior
        context.fill(
                offsetX,
                offsetY + size - 1,
                offsetX + size,
                offsetY + size,
                borderColor
        );
        // Borde izquierdo
        context.fill(
                offsetX,
                offsetY,
                offsetX + 1,
                offsetY + size,
                borderColor
        );
        // Borde derecho
        context.fill(
                offsetX + size - 1,
                offsetY,
                offsetX + size,
                offsetY + size,
                borderColor
        );
    }

    private int darkenColor(int color) {
        // Extraer componentes RGB
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Oscurecer cada componente (multiplicar por 0.7)
        r = (int) (r * 0.7);
        g = (int) (g * 0.7);
        b = (int) (b * 0.7);

        // Recomponer el color
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}