package com.github.razorplay01.geowaremod.tetris.game.piece;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Tetromino {
    protected TetrominoBlock[] blocks;
    protected int centerX;
    protected int centerY;
    protected int rotation; // 0, 1, 2, 3 (0° a 270°)

    protected Tetromino(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.rotation = 0;
        initializeBlocks();
    }

    protected abstract Tetromino createInstance(int centerX, int centerY);

    public Tetromino clone() {
        Tetromino clone = createInstance(this.centerX, this.centerY);
        clone.rotation = this.rotation;
        clone.blocks = new TetrominoBlock[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            clone.blocks[i] = blocks[i].clone();
        }
        return clone;
    }

    // Método abstracto que cada tipo de pieza implementará
    protected abstract void initializeBlocks();

    // Método para rotar la pieza 90 grados en sentido horario
    public void rotate() {
        rotation = (rotation + 1) % 4;
        for (TetrominoBlock block : blocks) {
            int oldX = block.getX();
            int oldY = block.getY();
            block.setX(-oldY);
            block.setY(oldX);
        }
    }

    public void moveLeft() {
        centerX--;
    }

    public void moveRight() {
        centerX++;
    }

    public void moveDown() {
        centerY++;
    }

    public List<Vec2f> getAbsolutePositions() {
        List<Vec2f> positions = new ArrayList<>();
        for (TetrominoBlock block : blocks) {
            positions.add(new Vec2f(
                    ((float) centerX + block.getX()),
                    ((float) centerY + block.getY())
            ));
        }
        return positions;
    }


    public void render(DrawContext context, int screenX, int screenY, int size) {
        for (TetrominoBlock block : blocks) {
            int x = screenX + (centerX + block.getX()) * size;
            int y = screenY + (centerY + block.getY()) * size;
            block.render(context, x, y, size);
        }
    }
}