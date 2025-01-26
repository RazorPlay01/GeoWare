package com.github.razorplay01.geoware.tetris.game.board;

import com.github.razorplay01.geoware.tetris.game.piece.Tetromino;
import com.github.razorplay01.geoware.tetris.game.piece.TetrominoBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec2f;

import java.util.Collections;
import java.util.List;

public class GameBoard {
    /*
    private static final int WIDTH = 28;
    private static final int HEIGHT = 32;
    private static final int BLOCK_SIZE = 8;
    */

    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final int BLOCK_SIZE = 16;
    private TetrominoBlock[][] grid;

    public GameBoard() {
        grid = new TetrominoBlock[HEIGHT][WIDTH];
    }

    public void render(DrawContext context, int offsetX, int offsetY) {
        // Dibujar el borde del tablero
        context.fill(offsetX - 2, offsetY - 2,
                offsetX + WIDTH * BLOCK_SIZE + 2,
                offsetY + HEIGHT * BLOCK_SIZE + 2,
                0xFF101010); // Borde blanco

        // Dibujar el fondo del tablero
        context.fill(offsetX, offsetY,
                offsetX + WIDTH * BLOCK_SIZE,
                offsetY + HEIGHT * BLOCK_SIZE,
                0xFF373737);

        // Dibujar los bloques
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] != null) {
                    grid[y][x].render(context,
                            offsetX + x * BLOCK_SIZE,
                            offsetY + y * BLOCK_SIZE,
                            BLOCK_SIZE);
                }
            }
        }
    }

    public static int getBlockSize() {
        return BLOCK_SIZE;
    }

    public boolean isLineFull(int y) {
        for (int x = 0; x < WIDTH; x++) {
            if (grid[y][x] == null) return false;
        }
        return true;
    }

    public void clearLines(List<Integer> linesToClear) {
        if (linesToClear.isEmpty()) return;

        // Ordenamos las líneas de abajo hacia arriba
        linesToClear.sort(Collections.reverseOrder());

        // Crear una nueva cuadrícula temporal
        TetrominoBlock[][] newGrid = new TetrominoBlock[HEIGHT][WIDTH];

        // Índice para la nueva cuadrícula
        int newY = HEIGHT - 1;

        // Copiar las líneas que no están completas a la nueva cuadrícula
        for (int y = HEIGHT - 1; y >= 0; y--) {
            if (!linesToClear.contains(y)) {
                for (int x = 0; x < WIDTH; x++) {
                    newGrid[newY][x] = grid[y][x];
                }
                newY--;
            }
        }

        // Reemplazar la cuadrícula antigua con la nueva
        grid = newGrid;
    }

    public boolean isPositionValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && grid[y][x] == null;
    }

    public void placePiece(Tetromino piece) {
        List<Vec2f> positions = piece.getAbsolutePositions();
        for (Vec2f pos : positions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
                grid[y][x] = new TetrominoBlock(x, y, piece.getBlocks()[0].getColor());
            }
        }
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}