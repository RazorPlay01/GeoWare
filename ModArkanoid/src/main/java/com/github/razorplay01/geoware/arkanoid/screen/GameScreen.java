package com.github.razorplay01.geoware.arkanoid.screen;

import com.github.razorplay01.geoware.arkanoid.game.stages.TestGame;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Getter
public class GameScreen extends Screen {
    private final TestGame testGame = new TestGame(this);
    private int actualScore = 0;
    private Integer screenXPos;
    private Integer screenYPos;

    private final long gameTimeLimit; // en milisegundos
    private long gameStartTime;
    private boolean timeUp = false;

    private boolean showingEndGame = false;
    private long endGameStartTime;
    private static final long END_GAME_DURATION = 5000;

    public GameScreen(int actualScore, int timeLimitSeconds) {
        super(Text.empty());
        this.actualScore = actualScore;
        this.gameTimeLimit = timeLimitSeconds * 1000L;
    }

    @Override
    protected void init() {
        if (screenXPos == null || screenYPos == null) {
            this.screenXPos = (width / 2) - (testGame.getScreenWidth() / 2);
            this.screenYPos = (height / 2) - (testGame.getScreenHeight() / 2);
            testGame.init();
            this.gameStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        testGame.renderBackground(context, mouseX, mouseY, delta);
        testGame.render(context, mouseX, mouseY, delta);
        testGame.updateAndRenderPlayer(context, mouseX, mouseY, delta);

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
                    screenXPos + testGame.getScreenWidth() + 10,
                    screenYPos + 20, // Posición debajo del score
                    0xFFFFFFFF,
                    true
            );
        }

        if (showingEndGame) {
            renderEndGameScreen(context);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
            testGame.getPlayer().moveRight();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            testGame.getPlayer().stopMovingLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            testGame.getPlayer().stopMovingRight();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;

        // Verificar si se acabó el tiempo
        if (elapsedTime >= gameTimeLimit && !timeUp) {
            timeUp = true;
            testGame.getPlayer().setLosing(true);
        }

        if ((testGame.getPlayer().isLosing() || testGame.getPlayer().isWinning() || timeUp) && !showingEndGame) {
            showingEndGame = true;
            endGameStartTime = currentTime;
        }

        if (showingEndGame && currentTime - endGameStartTime > END_GAME_DURATION) {
            this.close();
        }

        super.tick();
    }

    private void renderEndGameScreen(DrawContext context) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Fondo semi-transparente
        context.fill(0, 0, this.width, this.height, 0xCC000000);

        // Mensajes
        String endMessage = timeUp ? "¡TIME'S UP!" : "¡GAME ENDED!";
        String scoreMessage = "Game Score: " + testGame.getPlayer().getScore();
        String prevScoreMessage = "Prev Score: " + this.actualScore;
        String totalScoreMessage = "Total Score: " + (this.actualScore + testGame.getPlayer().getScore());
        String timeMessage = String.format("Closing in %.1f seconds...",
                (END_GAME_DURATION - (System.currentTimeMillis() - endGameStartTime)) / 1000.0);

        int messageWidth = this.textRenderer.getWidth(endMessage);
        int scoreWidth = this.textRenderer.getWidth(scoreMessage);
        int prevScoreWidth = this.textRenderer.getWidth(totalScoreMessage);
        int totalScoreWidth = this.textRenderer.getWidth(totalScoreMessage);
        int timeWidth = this.textRenderer.getWidth(timeMessage);

        // Renderizar todos los mensajes centrados
        context.drawText(this.textRenderer, endMessage,
                centerX - messageWidth / 2, centerY - 40, 0xFFFFFF00, true);
        context.drawText(this.textRenderer, scoreMessage,
                centerX - scoreWidth / 2, centerY, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, prevScoreMessage,
                centerX - prevScoreWidth / 2, centerY + 20, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, totalScoreMessage,
                centerX - totalScoreWidth / 2, centerY + 40, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, timeMessage,
                centerX - timeWidth / 2, centerY + 70, 0xFFAAAAAA, true);
    }
}
