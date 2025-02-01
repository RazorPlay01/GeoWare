package com.github.razorplay01.geoware.donkeykong.game.stages;

import com.github.razorplay01.geoware.donkeykong.game.entity.Fire;
import com.github.razorplay01.geoware.donkeykong.game.entity.Particle;
import com.github.razorplay01.geoware.donkeykong.game.entity.barrel.DonkeyKongEntity;
import com.github.razorplay01.geoware.donkeykong.game.entity.item.ItemEntity;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.geoware.donkeykong.game.entity.barrel.Barrel;
import com.github.razorplay01.geoware.donkeykong.game.entity.player.Player;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Game implements IGame {
    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final List<Barrel> barrels = new ArrayList<>();
    protected final List<Ladder> ladders = new ArrayList<>();
    protected final List<Platform> platforms = new ArrayList<>();
    protected final List<VictoryZone> victoryPlatforms = new ArrayList<>();
    protected final List<Fire> fires = new ArrayList<>();
    protected final List<ItemEntity> items = new ArrayList<>();
    protected final List<Particle> particles = new ArrayList<>();
    protected Player player;
    protected DonkeyKongEntity donkeyKong;
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

    @Override
    public void updateAndRenderPlayer(DrawContext context, int mouseX, int mouseY, float delta) {
        getPlayer().update();
        getPlayer().render(context);
    }

    /**
     * Crea una línea de plataformas.
     *
     * @param startX     Posición X.
     * @param startY     Posición Y.
     * @param segments   Número de segmentos de la plataforma.
     * @param directionX Dirección horizontal: -1 para izquierda, 0 para ninguna, 1 para derecha.
     * @param slopeY     Pendiente vertical: > 0 para descendente, < 0 para ascendente, 0 para horizontal.
     */
    public void createPlatformLine(float startX, float startY, float platformWidth, float platformHeight, int segments, int directionX, float slopeY) {
        float currentX = startX;
        float currentY = startY;
        for (int i = 0; i < segments; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX += directionX * platformWidth;
            currentY += slopeY;
        }
    }

    /**
     * Crea una escalera.
     *
     * @param xPos                   Posición X.
     * @param yPos                   Posición Y.
     * @param width                  Ancho.
     * @param height                 Alto.
     * @param canPassThroughPlatform Indica si se puede atravesar la plataforma.
     */
    public void createLadder(float xPos, float yPos, float width, float height, boolean canPassThroughPlatform) {
        this.getLadders().add(new Ladder(xPos, yPos, width, height, canPassThroughPlatform, screen));
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
                player.setLosing(true);
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