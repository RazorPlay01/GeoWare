package com.github.razorplay01.geowaremod.games.tetris.game.piece.pieces;


import com.github.razorplay01.geowaremod.games.tetris.BlockColor;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.TetrominoBlock;

// Pieza I (||||)
public class TetrominoI extends Tetromino {
    public TetrominoI(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoI(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(-1, 0, BlockColor.CYAN); // Cyan
        blocks[1] = new TetrominoBlock(0, 0, BlockColor.CYAN);
        blocks[2] = new TetrominoBlock(1, 0, BlockColor.CYAN);
        blocks[3] = new TetrominoBlock(2, 0, BlockColor.CYAN);
    }
}