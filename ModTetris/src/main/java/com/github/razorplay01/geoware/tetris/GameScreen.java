package com.github.razorplay01.geoware.tetris;

import com.github.razorplay01.geoware.tetris.game.TetrisGame;
import com.github.razorplay01.geoware.tetris.game.board.GameBoard;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GameScreen extends Screen {
    private final TetrisGame game;

    private final long gameTimeLimit; // en milisegundos
    private long gameStartTime;
    private boolean timeUp = false;

    private boolean showingEndGame = false;
    private long endGameStartTime;
    private static final long END_GAME_DURATION = 5000;

    public GameScreen(float speedMultiplier, int timeLimitSeconds) {
        super(Text.empty());
        this.game = new TetrisGame(speedMultiplier);
        this.gameTimeLimit = timeLimitSeconds * 1000L; // Convertir segundos a milisegundos
        this.gameStartTime = System.currentTimeMillis(); // Iniciar el temporizador
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int screenWidth = this.width;
        int screenHeight = this.height;
        int boardWidth = game.getBoard().getWidth() * GameBoard.getBlockSize();
        int boardHeight = game.getBoard().getHeight() * GameBoard.getBlockSize();

        int boardX = (screenWidth - boardWidth) / 2;
        int boardY = (screenHeight - boardHeight) / 2;

        // Mostrar tiempo restante
        if (!showingEndGame) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - gameStartTime;
            long remainingTime = Math.max(0, gameTimeLimit - elapsedTime);
            int seconds = (int) (remainingTime / 1000);

            String timeText = String.format("Time: %d:%02d", seconds / 60, seconds % 60);
            context.drawText(
                    this.textRenderer,
                    timeText,
                    boardX + boardWidth + 20, // Posición junto al tablero
                    boardY + 140, // Debajo de la puntuación
                    0xFFFFFFFF,
                    true
            );
        }

        renderGame(context, boardX, boardY, boardWidth);

        if (game.isPaused()) {
            renderCenteredMessage(context, "PAUSED", 0xFFFFFFFF);
        }

        if (showingEndGame) {
            renderEndGameScreen(context);
        }
    }

    private void renderGame(DrawContext context, int boardX, int boardY, int boardWidth) {
        game.getBoard().render(context, boardX, boardY);

        if (game.getCurrentPiece() != null) {
            game.getCurrentPiece().render(context, boardX, boardY, GameBoard.getBlockSize());
        }

        // Renderizar siguiente pieza y puntuaciones
        int nextPieceX = boardX + boardWidth + 20;
        context.drawText(this.textRenderer, "Next:", nextPieceX, boardY, 0xFFFFFF, true);
        if (game.getNextPiece() != null) {
            game.getNextPiece().render(context, nextPieceX, boardY + 20, GameBoard.getBlockSize());
        }

        context.drawText(this.textRenderer, "Score: " + game.getScore(),
                nextPieceX, boardY + 100, 0xFFFFFF, true);
        context.drawText(this.textRenderer, "Speed: " + String.format("%.1fx", game.getCurrentSpeedMultiplier()),
                nextPieceX, boardY + 120, 0xFFFFFF, true);
    }

    private void renderCenteredMessage(DrawContext context, String message, int color) {
        int textWidth = this.textRenderer.getWidth(message);
        int textX = (this.width - textWidth) / 2;
        int textY = this.height / 2;

        // Dibujar un fondo semi-transparente
        context.fill(textX - 10, textY - 10,
                textX + textWidth + 10, textY + 20,
                0x88000000);

        context.drawText(this.textRenderer, message, textX, textY, color, true);
    }

    private void renderEndGameScreen(DrawContext context) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Fondo semi-transparente
        context.fill(0, 0, this.width, this.height, 0xCC000000);

        // Mensajes
        String endMessage = timeUp ? "¡TIME'S UP!" : "¡GAME ENDED!";
        String scoreMessage = "Game Score: " + game.getScore();
        String totalScoreMessage = "Total Score: " + game.getScore(); // Puedes ajustar esto si tienes un puntaje total
        String timeMessage = String.format("Closing in %.1f seconds...",
                (END_GAME_DURATION - (System.currentTimeMillis() - endGameStartTime)) / 1000.0);

        int messageWidth = this.textRenderer.getWidth(endMessage);
        int scoreWidth = this.textRenderer.getWidth(scoreMessage);
        int totalScoreWidth = this.textRenderer.getWidth(totalScoreMessage);
        int timeWidth = this.textRenderer.getWidth(timeMessage);

        // Renderizar todos los mensajes centrados
        context.drawText(this.textRenderer, endMessage,
                centerX - messageWidth / 2, centerY - 40, 0xFFFFFF00, true);
        context.drawText(this.textRenderer, scoreMessage,
                centerX - scoreWidth / 2, centerY, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, totalScoreMessage,
                centerX - totalScoreWidth / 2, centerY + 20, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, timeMessage,
                centerX - timeWidth / 2, centerY + 50, 0xFFAAAAAA, true);
    }

    @Override
    public void tick() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;

        // Verificar si se acabó el tiempo
        if (elapsedTime >= gameTimeLimit && !timeUp) {
            timeUp = true;
            game.endGame(); // Finalizar el juego
        }

        if ((game.isGameOver() || game.isShouldClose() || timeUp) && !showingEndGame) {
            showingEndGame = true;
            endGameStartTime = currentTime;
        }

        if (showingEndGame && currentTime - endGameStartTime > END_GAME_DURATION) {
            this.close();
        }

        if (!showingEndGame) {
            game.tick(currentTime);
        }
        super.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!showingEndGame) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> game.moveLeft();
                case GLFW.GLFW_KEY_RIGHT -> game.moveRight();
                case GLFW.GLFW_KEY_DOWN -> game.moveDown();
                case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_SPACE -> game.rotate();
                case GLFW.GLFW_KEY_ENTER -> game.hardDrop();
                case GLFW.GLFW_KEY_ESCAPE -> {
                    game.endGame();
                    showingEndGame = true;
                    endGameStartTime = System.currentTimeMillis();
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
