/*
package com.github.razorplay01.geoware.donkeykong.game.stages;

import com.github.razorplay01.geoware.donkeykong.DonkeyKong;
import com.github.razorplay01.geoware.donkeykong.game.entity.Fire;
import com.github.razorplay01.geoware.donkeykong.game.entity.Particle;
import com.github.razorplay01.geoware.donkeykong.game.entity.barrel.DonkeyKongEntity;
import com.github.razorplay01.geoware.donkeykong.game.entity.item.HammetItem;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.geoware.donkeykong.game.entity.player.Player;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.List;

@Getter
public class TestGame extends Game {
    @Setter
    private boolean showErrorMessage = false;
    private final TextRenderer textRenderer = client.textRenderer;

    private static final int PLATFORM_WIDTH = 16;
    private static final int PLATFORM_HEIGHT = 8;

    private boolean showingEndGame = false;
    private long endGameStartTime;
    private static final long END_GAME_DURATION = 5000;
    private final int previousScore;
    private final int timeLimitSeconds;

    public TestGame(GameScreen screen, int previousScore, int timeLimitSeconds) {
        super(Identifier.of(DonkeyKong.MOD_ID, "textures/gui/map_base.png"), screen);
        this.previousScore = previousScore;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    @Override
    public void init() {
        if (platforms.isEmpty()) {
            createGameMap();
        }
        this.player = new Player(screen.getScreenXPos() + 36f, screen.getScreenYPos() + this.getScreenHeight() - 16 - platforms.getFirst().getHeight(), screen);
        this.donkeyKong = new DonkeyKongEntity(screen.getScreenXPos() + 18f, screen.getScreenYPos() + 52f, screen, 80, 0.7f);
        this.items.add(new HammetItem(screen.getScreenXPos() + 167f, screen.getScreenYPos() + 190f, 13, 13, screen));
        this.items.add(new HammetItem(screen.getScreenXPos() + 16f, screen.getScreenYPos() + 92f, 13, 13, screen));
        //this.fires.add(new Fire(screen.getScreenXPos() + 10f, screen.getScreenYPos() + this.getScreenHeight() - 16 - platforms.getFirst().getHeight(), screen));
        this.victoryPlatforms.add(new VictoryZone(screen, screen.getScreenXPos() + 88f, screen.getScreenYPos() + 36f, 48, 20, 0xAAFFFFFF));
    }



    private void endGameHandle(DrawContext context) {
        if (!gameEnded && (player.isWinning() || player.isLosing())) {
            gameEnded = true;
            if (player.isWinning()) {
                finalScore = displayValue + player.getScore();
            } else {
                finalScore = player.getScore();
            }
            showingEndGame = true;
            endGameStartTime = System.currentTimeMillis();
        }

        if (showingEndGame) {
            renderEndGameScreen(context);

            // Cerrar la pantalla después del tiempo especificado
            if (System.currentTimeMillis() - endGameStartTime >= END_GAME_DURATION) {
                MinecraftClient.getInstance().setScreen(null);
            }
        }
    }

    public void renderTime(DrawContext context, TextRenderer textRenderer) {
        renderTime(context, textRenderer, timeLimitSeconds,
                screen.getScreenXPos() + 10, screen.getScreenYPos() + 30, 1.0f);
    }

    private void renderEndGameScreen(DrawContext context) {
        // Fondo semi-transparente
        context.fill(0, 0, screen.width, screen.height, 0xCC000000);

        int centerX = screen.width / 2;
        int centerY = screen.height / 2;

        // Preparar mensajes
        String endMessage = player.isWinning() ? "¡YOU WIN!" : "¡GAME OVER!";
        String scoreMessage = "Game Score: " + player.getScore();
        String prevScoreMessage = "Prev Score: " + previousScore;
        String totalScoreMessage = "Total Score: " + (previousScore + finalScore);
        String timeMessage = String.format("Closing in %.1f seconds...",
                (END_GAME_DURATION - (System.currentTimeMillis() - endGameStartTime)) / 1000.0);

        // Calcular anchos para centrado
        int messageWidth = textRenderer.getWidth(endMessage);
        int scoreWidth = textRenderer.getWidth(scoreMessage);
        int prevScoreWidth = textRenderer.getWidth(prevScoreMessage);
        int totalScoreWidth = textRenderer.getWidth(totalScoreMessage);
        int timeWidth = textRenderer.getWidth(timeMessage);

        // Renderizar mensajes
        context.drawText(textRenderer, endMessage,
                centerX - messageWidth / 2, centerY - 40, 0xFFFFFF00, true);
        context.drawText(textRenderer, scoreMessage,
                centerX - scoreWidth / 2, centerY, 0xFFFFFFFF, true);
        context.drawText(textRenderer, prevScoreMessage,
                centerX - prevScoreWidth / 2, centerY + 20, 0xFFFFFFFF, true);
        context.drawText(textRenderer, totalScoreMessage,
                centerX - totalScoreWidth / 2, centerY + 40, 0xFFFFFFFF, true);
        context.drawText(textRenderer, timeMessage,
                centerX - timeWidth / 2, centerY + 70, 0xFFAAAAAA, true);
    }

    private void initialDelayHandle(DrawContext context) {
        if (!gameStarted) {
            if (gameStartTime == 0) {
                gameStartTime = System.currentTimeMillis();
                initialDelay = 5;
            }

            long currentTime = System.currentTimeMillis();
            if ((currentTime - gameStartTime) / 1000 >= initialDelay) {
                gameStarted = true;
            }

            String waitMessage = "Starting in " + (initialDelay - (currentTime - gameStartTime) / 1000);
            context.drawText(
                    textRenderer,
                    waitMessage,
                    screen.getScreenXPos() + getScreenWidth() / 2 - textRenderer.getWidth(waitMessage) / 2,
                    screen.getScreenYPos() + getScreenHeight() / 2,
                    0xFFFFFF,
                    true
            );
        }
    }



    @Override
    public int getScreenWidth() {
        return 224;
    }

    @Override
    public int getScreenHeight() {
        return 256;
    }
}
*/
