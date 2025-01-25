package com.github.razorplay01.tetrismod.game.piece.pieces;

import com.github.razorplay01.tetrismod.game.piece.Tetromino;
import com.github.razorplay01.tetrismod.game.piece.TetrominoBlock;

// Pieza J
public class TetrominoJ extends Tetromino {
    public TetrominoJ(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoJ(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(0, 0, 0xFF0000FF); // Azul
        blocks[1] = new TetrominoBlock(-1, 0, 0xFF0000FF);
        blocks[2] = new TetrominoBlock(1, 0, 0xFF0000FF);
        blocks[3] = new TetrominoBlock(-1, 1, 0xFF0000FF);
    }
}
