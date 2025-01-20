package com.github.razorplay01.donkeykongfabric.game.stages;

import com.github.razorplay01.donkeykongfabric.game.entity.barrel.BarrelSpawner;
import com.github.razorplay01.donkeykongfabric.game.mapobject.VictoryZone;
import com.github.razorplay01.donkeykongfabric.game.entity.barrel.Barrel;
import com.github.razorplay01.donkeykongfabric.game.entity.player.Player;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
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
    protected final List<BarrelSpawner> barrelSpawners = new ArrayList<>();
    protected final List<VictoryZone> victoryPlatforms = new ArrayList<>();
    protected Player player;
    protected final Identifier backgroundImage;
    protected final GameScreen screen;

    private long startTime;
    private int totalSeconds;
    private int displayValue;
    private boolean timeInitialized = false;

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
            this.getPlatforms().add(new Platform(currentX, currentY, platformWidth, platformHeight));
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
        this.getLadders().add(new Ladder(xPos, yPos, width, height, canPassThroughPlatform));
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
            this.startTime = System.currentTimeMillis();
            this.totalSeconds = totalSeconds;
            this.displayValue = (totalSeconds / 2) * 100;
            this.timeInitialized = true;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTimeSeconds = (currentTime - startTime) / 1000;

        int newValue = (totalSeconds - (int) elapsedTimeSeconds) / 2 * 100;
        this.displayValue = Math.max(newValue, 0);

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