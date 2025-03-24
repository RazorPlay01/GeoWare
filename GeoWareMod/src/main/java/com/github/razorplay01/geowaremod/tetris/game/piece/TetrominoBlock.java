package com.github.razorplay01.geowaremod.tetris.game.piece;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.tetris.BlockColor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class TetrominoBlock implements Cloneable {
    private int x;
    private int y;
    private BlockColor color;

    public TetrominoBlock(int x, int y, BlockColor color) {
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
        Identifier texture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/tetris/blocks.png");
        context.drawTexture(texture, offsetX, offsetY, size, size, this.color.getU(), this.color.getV(), 9, 9, 63, 9);
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