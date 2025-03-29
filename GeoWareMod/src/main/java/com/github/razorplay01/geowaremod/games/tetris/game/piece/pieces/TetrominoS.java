package com.github.razorplay01.geowaremod.games.tetris.game.piece.pieces;

import com.github.razorplay01.geowaremod.games.tetris.BlockColor;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.TetrominoBlock;

// Pieza S
public class TetrominoS extends Tetromino {
    public TetrominoS(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoS(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(0, 0, BlockColor.GREEN); // Verde
        blocks[1] = new TetrominoBlock(-1, 0, BlockColor.GREEN);
        blocks[2] = new TetrominoBlock(0, 1, BlockColor.GREEN);
        blocks[3] = new TetrominoBlock(1, 1, BlockColor.GREEN);
    }
}
