package com.github.razorplay01.geowaremod.tetris.game.piece.pieces;

import com.github.razorplay01.geowaremod.tetris.BlockColor;
import com.github.razorplay01.geowaremod.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.tetris.game.piece.TetrominoBlock;

// Pieza T
public class TetrominoT extends Tetromino {
    public TetrominoT(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoT(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(0, 0, BlockColor.PURPLE); // Púrpura
        blocks[1] = new TetrominoBlock(-1, 0, BlockColor.PURPLE);
        blocks[2] = new TetrominoBlock(1, 0, BlockColor.PURPLE);
        blocks[3] = new TetrominoBlock(0, 1, BlockColor.PURPLE);
    }
}
