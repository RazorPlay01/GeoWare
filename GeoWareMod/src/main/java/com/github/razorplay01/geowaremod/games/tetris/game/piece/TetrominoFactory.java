package com.github.razorplay01.geowaremod.games.tetris.game.piece;

import com.github.razorplay01.geowaremod.games.tetris.game.piece.pieces.*;

import java.util.Random;

public class TetrominoFactory {
    private static final Random random = new Random();

    public static Tetromino createRandomTetromino(int centerX, int centerY) {
        int pieceType = random.nextInt(7);
        return switch (pieceType) {
            case 0 -> new TetrominoI(centerX, centerY);
            case 1 -> new TetrominoO(centerX, centerY);
            case 2 -> new TetrominoT(centerX, centerY);
            case 3 -> new TetrominoL(centerX, centerY);
            case 4 -> new TetrominoJ(centerX, centerY);
            case 5 -> new TetrominoS(centerX, centerY);
            case 6 -> new TetrominoZ(centerX, centerY);
            default -> throw new IllegalStateException("Invalid piece type: " + pieceType);
        };
    }
}