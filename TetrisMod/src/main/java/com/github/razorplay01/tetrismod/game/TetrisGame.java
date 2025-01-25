package com.github.razorplay01.tetrismod.game;

import com.github.razorplay01.tetrismod.game.board.GameBoard;
import com.github.razorplay01.tetrismod.game.piece.Tetromino;
import com.github.razorplay01.tetrismod.game.piece.TetrominoFactory;
import lombok.Getter;
import net.minecraft.util.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TetrisGame {
    private final GameBoard board;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int score;
    private int level;
    private boolean isGameOver;
    private boolean isPaused;
    private long lastDropTime;
    private long dropInterval;
    private final float initialSpeedMultiplier;
    private float currentSpeedMultiplier;
    private static final long BASE_DROP_INTERVAL = 1000; // 1 segundo base
    private boolean shouldClose = false;

    public TetrisGame(float speedMultiplier) {
        this.initialSpeedMultiplier = speedMultiplier;
        this.currentSpeedMultiplier = speedMultiplier;
        this.dropInterval = (long) (BASE_DROP_INTERVAL / speedMultiplier);
        this.board = new GameBoard();
        this.score = 0;
        this.level = 1;
        this.isGameOver = false;
        this.isPaused = false;
        this.dropInterval = 1000;
        spawnNewPiece();
    }

    public void tick(long currentTime) {
        if (isGameOver || isPaused) return;

        if (currentTime - lastDropTime > dropInterval) {
            if (canMoveDown()) {
                currentPiece.moveDown();
            } else {
                placePiece();
                clearLines();
                spawnNewPiece();
            }
            lastDropTime = currentTime;
        }
    }

    private void spawnNewPiece() {
        if (nextPiece == null) {
            nextPiece = TetrominoFactory.createRandomTetromino(board.getWidth() / 2, 0);
        }
        currentPiece = nextPiece;
        nextPiece = TetrominoFactory.createRandomTetromino(board.getWidth() / 2, 0);

        if (checkCollision(currentPiece)) {
            isGameOver = true;
        }
    }

    private boolean canMoveDown() {
        Tetromino test = currentPiece.clone();
        test.moveDown();
        return !checkCollision(test);
    }

    private boolean checkCollision(Tetromino piece) {
        List<Vec2f> positions = piece.getAbsolutePositions();
        for (Vec2f pos : positions) {
            int x = (int) pos.x;
            int y = (int) pos.y;
            if (x < 0 || x >= board.getWidth() || y >= board.getHeight()) {
                return true;
            }
            if (y >= 0 && !board.isPositionValid(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void placePiece() {
        board.placePiece(currentPiece);
    }

    private void clearLines() {
        List<Integer> linesToClear = new ArrayList<>();

        // Identificar todas las líneas completas
        for (int y = board.getHeight() - 1; y >= 0; y--) {
            if (board.isLineFull(y)) {
                linesToClear.add(y);
            }
        }

        // Si hay líneas para limpiar
        if (!linesToClear.isEmpty()) {
            board.clearLines(linesToClear);
            updateScore(linesToClear.size());
        }
    }

    private void updateScore(int lines) {
        switch (lines) {
            case 2 -> score += 300;
            case 3 -> score += 500;
            case 4 -> score += 800;
            case 5 -> score += 1000;
            default -> score += 100;
        }
        level = (score / 200) + 1;
        updateDropInterval();
    }

    private void updateDropInterval() {
        // La velocidad aumenta con el nivel y se multiplica por el multiplicador inicial
        currentSpeedMultiplier = initialSpeedMultiplier * (1 + (level - 1) * 0.1f);
        dropInterval = (long) (BASE_DROP_INTERVAL / currentSpeedMultiplier);
    }

    public void moveLeft() {
        if (isGameOver || isPaused) return;
        Tetromino test = currentPiece.clone();
        test.moveLeft();
        if (!checkCollision(test)) {
            currentPiece.moveLeft();
        }
    }

    public void moveRight() {
        if (isGameOver || isPaused) return;
        Tetromino test = currentPiece.clone();
        test.moveRight();
        if (!checkCollision(test)) {
            currentPiece.moveRight();
        }
    }

    public void moveDown() {
        if (isGameOver || isPaused) return;
        if (canMoveDown()) {
            currentPiece.moveDown();
        } else {
            placePiece();
            clearLines();
            spawnNewPiece();
        }
    }

    public void rotate() {
        if (isGameOver || isPaused) return;
        Tetromino test = currentPiece.clone();
        test.rotate();
        if (!checkCollision(test)) {
            currentPiece.rotate();
        }
    }

    public void hardDrop() {
        if (isGameOver || isPaused) return;
        while (canMoveDown()) {
            currentPiece.moveDown();
        }
        placePiece();
        clearLines();
        spawnNewPiece();
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    public void endGame() {
        isGameOver = true;
        shouldClose = true;
        printGameResults();
    }

    private void printGameResults() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║           END  GAME!               ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ Final Score: " + String.format("%-19d", score) + "║");
        System.out.println("║ Level Reached: " + String.format("%-16d", level) + "║");
        System.out.println("╚════════════════════════════════════╝");
    }
}