package com.github.razorplay01.geowaremod.games.tetris.game.piece;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.tetris.BlockColor;
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
}