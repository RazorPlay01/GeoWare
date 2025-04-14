package com.github.razorplay01.geowaremod.games.tetris.game;

import com.github.razorplay01.geowaremod.games.tetris.game.board.GameBoard;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.Tetromino;
import com.github.razorplay01.geowaremod.games.tetris.game.piece.TetrominoFactory;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.Vec2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TetrisGame extends Game {
    private final GameBoard board = new GameBoard();
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int level;
    private long lastDropTime;
    private long dropInterval;
    private final float initialSpeedMultiplier;
    private float currentSpeedMultiplier;
    private static final long BASE_DROP_INTERVAL = 1000; // 1 segundo base

    public TetrisGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, float speedMultiplier) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.initialSpeedMultiplier = speedMultiplier;
        this.currentSpeedMultiplier = speedMultiplier;
        this.dropInterval = (long) (BASE_DROP_INTERVAL / speedMultiplier);
        this.level = 1;
        spawnNewPiece();
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE) {
            if (System.currentTimeMillis() - lastDropTime > dropInterval) {
                if (canMoveDown()) {
                    currentPiece.moveDown();
                } else {
                    placePiece();
                    clearLines();
                    spawnNewPiece();
                }
                lastDropTime = System.currentTimeMillis();
            }
        }
    }

    private void spawnNewPiece() {
        if (nextPiece == null) {
            nextPiece = TetrominoFactory.createRandomTetromino(board.getWidth() / 2, 0);
        }
        currentPiece = nextPiece;
        nextPiece = TetrominoFactory.createRandomTetromino(board.getWidth() / 2, 0);

        if (checkCollision(currentPiece)) {
            status = GameStatus.ENDING;
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

        if (!linesToClear.isEmpty()) {
            board.clearLines(linesToClear);
            updateScore(linesToClear.size());
        }
    }

    private void updateScore(int lines) {
        switch (lines) {
            case 2 -> addScore(3);
            case 3 -> addScore(5);
            case 4 -> addScore(8);
            case 5 -> addScore(10);
            default -> addScore(1);
        }
        level = (gameScore / 2) + 1;
        updateDropInterval();
    }

    private void updateDropInterval() {
        // La velocidad aumenta con el nivel y se multiplica por el multiplicador inicial
        currentSpeedMultiplier = initialSpeedMultiplier * (1 + (level - 1) * 0.1f);
        dropInterval = (long) (BASE_DROP_INTERVAL / currentSpeedMultiplier);
    }

    public void moveLeft() {
        Tetromino test = currentPiece.clone();
        test.moveLeft();
        if (!checkCollision(test)) {
            currentPiece.moveLeft();
        }
    }

    public void moveRight() {
        Tetromino test = currentPiece.clone();
        test.moveRight();
        if (!checkCollision(test)) {
            currentPiece.moveRight();
        }
    }

    public void moveDown() {
        if (canMoveDown()) {
            currentPiece.moveDown();
        } else {
            placePiece();
            clearLines();
            spawnNewPiece();
        }
    }

    public void rotate() {
        Tetromino test = currentPiece.clone();
        test.rotate();
        if (!checkCollision(test)) {
            currentPiece.rotate();
        }
    }

    public void hardDrop() {
        while (canMoveDown()) {
            currentPiece.moveDown();
        }
        placePiece();
        clearLines();
        spawnNewPiece();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int boardX = screen.getGameScreenXPos();
        int boardY = screen.getGameScreenYPos();

        getBoard().render(context, boardX, boardY);
        if (getCurrentPiece() != null) {
            getCurrentPiece().render(context, boardX, boardY, GameBoard.getBlockSize());
        }
        int nextPieceX = boardX + getScreenWidth() + 20;
        if (getNextPiece() != null) {
            getNextPiece().render(context, nextPieceX, boardY + 20, GameBoard.getBlockSize());
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawBasicBackground(screen);
    }

    @Override
    public int getScreenWidth() {
        return getBoard().getWidth() * GameBoard.getBlockSize();
    }

    @Override
    public int getScreenHeight() {
        return getBoard().getHeight() * GameBoard.getBlockSize();
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (status == GameStatus.ACTIVE) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_A -> moveLeft();
                case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_D -> moveRight();
                case GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_S -> moveDown();
                case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_W -> rotate();
                case GLFW.GLFW_KEY_SPACE -> hardDrop();
            }
        }
    }
}