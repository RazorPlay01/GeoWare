package com.github.razorplay01.geowaremod.games.tetris.game.piece.pieces;

import com.github.razorplay01.geowaremod.games.tetris.BlockColor;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.TetrominoBlock;

// Pieza Z
public class TetrominoZ extends Tetromino {
    public TetrominoZ(int centerX, int centerY) {
        super(centerX, centerY);
    }

    @Override
    protected Tetromino createInstance(int centerX, int centerY) {
        return new TetrominoZ(centerX, centerY);
    }

    @Override
    protected void initializeBlocks() {
        blocks = new TetrominoBlock[4];
        blocks[0] = new TetrominoBlock(0, 0, BlockColor.RED); // Rojo
        blocks[1] = new TetrominoBlock(1, 0, BlockColor.RED);
        blocks[2] = new TetrominoBlock(0, 1, BlockColor.RED);
        blocks[3] = new TetrominoBlock(-1, 1, BlockColor.RED);
    }
}
