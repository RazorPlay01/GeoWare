package com.github.razorplay01.geoware.arkanoid.game.stages;

import com.github.razorplay01.geoware.arkanoid.game.entity.Ball;
import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.game.entity.PowerUp;
import com.github.razorplay01.geoware.arkanoid.game.mapobject.Brick;
import com.github.razorplay01.geoware.arkanoid.game.util.BrickColor;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class Game implements IGame {
    protected static final Map<Character, BrickColor> BRICK_MAPPING = Map.of(
            'R', BrickColor.RED,
            'G', BrickColor.GREEN,
            'B', BrickColor.BLUE,
            'Y', BrickColor.YELLOW,
            'M', BrickColor.MAGENTA,
            'S', BrickColor.GRAY,
            'C', BrickColor.CYAN
    );
    protected final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;
    protected final List<Brick> bricks = new ArrayList<>();
    protected Player player;
    protected List<Ball> balls = new ArrayList<>();
    protected List<PowerUp> powerUps = new ArrayList<>();

    protected final Identifier backgroundImage;
    protected final GameScreen screen;

    protected long startTime = 0;
    protected int totalSeconds;
    protected int displayValue;
    protected boolean timeInitialized = false;

    protected boolean gameStarted = false;
    protected int initialDelay;
    protected long gameStartTime;
    protected boolean gameEnded = false;
    protected int finalScore = 0;

    protected Game(Identifier backgroundImage, GameScreen screen) {
        this.backgroundImage = backgroundImage;
        this.screen = screen;
    }


    /**
     * Renderiza la puntuación con ceros a la izquierda.
     *
     * @param context El contexto de dibujo.
     * @param score   La puntuación a renderizar.
     * @param x       La coordenada X donde se renderizará la puntuación.
     * @param y       La coordenada Y donde se renderizará la puntuación.
     * @param scale   La escala del texto.
     */
    public void renderScore(DrawContext context, TextRenderer textRenderer, int score, int x, int y, float scale) {
        String scoreText = String.format("%06d", score);
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(
                textRenderer,
                scoreText,
                (int) (x / scale),
                (int) (y / scale),
                0xFFFFFF,
                true
        );
        context.getMatrices().pop();
    }

    /**
     * Renderiza el tiempo con el formato especificado (SS00).
     *
     * @param context      El contexto de dibujo.
     * @param totalSeconds La cantidad total de segundos que durará el temporizador.
     * @param x            La coordenada X donde se renderizará el tiempo.
     * @param y            La coordenada Y donde se renderizará el tiempo.
     * @param scale        La escala del texto.
     */
    public void renderTime(DrawContext context, TextRenderer textRenderer, int totalSeconds, int x, int y, float scale) {
        if (!timeInitialized) {
            this.totalSeconds = totalSeconds;
            this.displayValue = (totalSeconds / 2) * 100;
            this.timeInitialized = true;
        }

        if (gameStarted && startTime == 0) {
            this.startTime = System.currentTimeMillis();
        }

        if (gameStarted && !gameEnded) {
            long currentTime = System.currentTimeMillis();
            long elapsedTimeSeconds = (currentTime - startTime) / 1000;

            int newValue = (totalSeconds - (int) elapsedTimeSeconds) / 2 * 100;
            this.displayValue = Math.max(newValue, 0);

            if (this.displayValue <= 0) {
                // player.setLosing(true);
            }
        }

        String timeText = String.format("%04d", this.displayValue);
        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(
                textRenderer,
                timeText,
                (int) (x / scale),
                (int) (y / scale),
                0xFFFFFF,
                true
        );
        context.getMatrices().pop();
    }
}