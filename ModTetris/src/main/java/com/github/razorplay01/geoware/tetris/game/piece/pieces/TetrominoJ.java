package com.github.razorplay01.geoware.tetris.game.piece.pieces;

import com.github.razorplay01.geoware.tetris.BlockColor;
import com.github.razorplay01.geoware.tetris.game.piece.Tetromino;
import com.github.razorplay01.geoware.tetris.game.piece.TetrominoBlock;

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
        blocks[0] = new TetrominoBlock(0, 0, BlockColor.BLUE); // Azul
        blocks[1] = new TetrominoBlock(-1, 0, BlockColor.BLUE);
        blocks[2] = new TetrominoBlock(1, 0, BlockColor.BLUE);
        blocks[3] = new TetrominoBlock(-1, 1, BlockColor.BLUE);
    }
}
