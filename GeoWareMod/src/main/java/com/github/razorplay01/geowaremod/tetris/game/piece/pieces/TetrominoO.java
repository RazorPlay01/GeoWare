package com.github.razorplay01.geowaremod.tetris.game.piece.pieces;

import com.github.razorplay01.geowaremod.tetris.BlockColor;
import com.github.razorplay01.geowaremod.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.tetris.game.piece.TetrominoBlock;

// Pieza O (cuadrado)
public class TetrominoO extends Tetromino {
    public TetrominoO(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoO(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(0, 0, BlockColor.YELLOW); // Amarillo
        blocks[1] = new TetrominoBlock(1, 0, BlockColor.YELLOW);
        blocks[2] = new TetrominoBlock(0, 1, BlockColor.YELLOW);
        blocks[3] = new TetrominoBlock(1, 1, BlockColor.YELLOW);
    }

    @Override
    public void rotate() {
        // La pieza O no rota
    }
}