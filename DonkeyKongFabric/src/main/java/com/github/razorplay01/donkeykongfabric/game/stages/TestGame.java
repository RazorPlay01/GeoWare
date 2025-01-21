package com.github.razorplay01.donkeykongfabric.game.stages;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.entity.Fire;
import com.github.razorplay01.donkeykongfabric.game.entity.HammetItem;
import com.github.razorplay01.donkeykongfabric.game.mapobject.VictoryZone;
import com.github.razorplay01.donkeykongfabric.game.entity.player.Player;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.entity.barrel.BarrelSpawner;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Getter
public class TestGame extends Game {
    @Setter
    private boolean showErrorMessage = false;
    private final TextRenderer textRenderer = client.textRenderer;

    private static final int PLATFORM_WIDTH = 16;
    private static final int PLATFORM_HEIGHT = 8;

    public TestGame(GameScreen screen) {
        super(Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/map_base.png"), screen);
        this.barrelSpawners.add(new BarrelSpawner(screen, 80, 0.7f));
    }

    @Override
    public void init() {
        if (this.getPlatforms().isEmpty() || this.getPlayer() == null) {
            createGameMap();
        }
        this.player = new Player(screen.getScreenXPos() + 36f, screen.getScreenYPos() + this.getScreenHeight() - 16 - platforms.getFirst().getHeight(), screen);
        this.items.add(new HammetItem(screen.getScreenXPos() + 167f, screen.getScreenYPos() + 190f, 13, 13, screen));
        this.items.add(new HammetItem(screen.getScreenXPos() + 16f, screen.getScreenYPos() + 92f, 13, 13, screen));
        this.fires.add(new Fire(screen.getScreenXPos() + 10f, screen.getScreenYPos() + this.getScreenHeight() - 16 - platforms.getFirst().getHeight(), screen));
        this.victoryPlatforms.add(new VictoryZone(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 36f, 48, 20, 0xAAFFFFFF));
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar elementos del juego
        for (VictoryZone victoryPlatform : getVictoryPlatforms()) {
            player.checkVictoryPlatform(victoryPlatform);
            victoryPlatform.render(context);
        }

        getPlatforms().forEach(platform -> platform.render(context));
        getLadders().forEach(ladder -> ladder.render(context));
        getItems().forEach(item -> item.render(context));

        // Siempre spawneamos barriles
        for (BarrelSpawner barrelSpawner : getBarrelSpawners()) {
            barrelSpawner.removeAndSpawnBarrels(context);
            barrelSpawner.update();
        }

        for (Fire fire : getFires()) {
            fire.render(context);
            fire.update();
        }

        // Siempre actualizamos y renderizamos al jugador
        updateAndRenderPlayer(context, mouseX, mouseY, delta);

        // Manejo del delay inicial
        if (!gameStarted) {
            if (gameStartTime == 0) {
                gameStartTime = System.currentTimeMillis();
                initialDelay = 5; // 3 segundos de delay inicial
            }

            long currentTime = System.currentTimeMillis();
            if ((currentTime - gameStartTime) / 1000 >= initialDelay) {
                gameStarted = true;
            }

            // Renderizar mensaje de espera
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

        // Verificar si el juego debe terminar
        if (!gameEnded && (getPlayer().isWinning() || getPlayer().isLosing())) {
            gameEnded = true;
            if (getPlayer().isWinning()) {
                finalScore = displayValue + getPlayer().getScore();
            } else {
                finalScore = getPlayer().getScore();
            }

            // Programar el cierre de la pantalla
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Esperar 3 segundos
                    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Si el juego ha terminado, mostrar mensaje de fin
        if (gameEnded) {
            String endMessage = getPlayer().isWinning() ? "You Win!" : "Game Over";
            String scoreMessage = "Final Score: " + finalScore;

            context.drawText(
                    textRenderer,
                    endMessage,
                    screen.getScreenXPos() + getScreenWidth() / 2 - textRenderer.getWidth(endMessage) / 2,
                    screen.getScreenYPos() + getScreenHeight() / 2,
                    0xFFFFFF,
                    true
            );

            context.drawText(
                    textRenderer,
                    scoreMessage,
                    screen.getScreenXPos() + getScreenWidth() / 2 - textRenderer.getWidth(scoreMessage) / 2,
                    screen.getScreenYPos() + getScreenHeight() / 2 + 20,
                    0xFFFFFF,
                    true
            );
        }

        // Siempre renderizar score y tiempo
        renderScore(context, textRenderer, getPlayer().getScore(), screen.getScreenXPos() + 10, screen.getScreenYPos() + 40, 1.0f);
        renderTime(context, textRenderer, 60, screen.getScreenXPos() + 10, screen.getScreenYPos() + 60, 1.0f);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(
                getBackgroundImage(),
                screen.getScreenXPos(),
                screen.getScreenYPos(),
                0,
                0,
                getScreenWidth(),
                getScreenHeight(),
                getScreenWidth(),
                getScreenHeight()
        );
    }

    @Override
    public int getScreenWidth() {
        return 224;
    }

    @Override
    public int getScreenHeight() {
        return 256;
    }

    @Override
    public void createGameMap() {
        createFirstPlatform();
        createSecondPlatform();
        createThirdPlatform();
        createFourthPlatform();
        createFifthPlatform();
        createSixthPlatform();
        createFinalPlatform();
    }

    private void createFirstPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine(screen.getScreenXPos(), bottomY, PLATFORM_WIDTH, PLATFORM_HEIGHT, 7, 1, 0);
        createPlatformLine((float) screen.getScreenXPos() + (7 * PLATFORM_WIDTH), bottomY - 1, PLATFORM_WIDTH, PLATFORM_HEIGHT, 7, 1, -1f);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 240f, 8, 8, false);
    }

    private void createSecondPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine(screen.getScreenXPos(), bottomY - 40, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, 1, 1f);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 213f, 8, 11, true);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 219f, 8, 24, true);
    }

    private void createThirdPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine((float) screen.getScreenXPos() + getScreenWidth() - PLATFORM_WIDTH, bottomY - 73f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, -1, 1f);
        createLadder(screen.getScreenXPos() + 96f, screen.getScreenYPos() + 182f, 8, 32, true);
        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 176f, 8, 8, false);
        createLadder(screen.getScreenXPos() + 32f, screen.getScreenYPos() + 186f, 8, 24, true);
    }

    private void createFourthPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine(screen.getScreenXPos(), bottomY - 106f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, 1, 1f);

        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 146f, 8, 14, true);
        createLadder(screen.getScreenXPos() + 112f, screen.getScreenYPos() + 149f, 8, 32, true);
        createLadder(screen.getScreenXPos() + 168f, screen.getScreenYPos() + 144f, 8, 8, false);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 153f, 8, 24, true);
    }

    private void createFifthPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine((float) screen.getScreenXPos() + getScreenWidth() - PLATFORM_WIDTH, bottomY - 139f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 13, -1, 1f);

        createLadder(screen.getScreenXPos() + 168f, screen.getScreenYPos() + 112f, 8, 16, true);
        createLadder(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 104f, 8, 13, false);
        createLadder(screen.getScreenXPos() + 72f, screen.getScreenYPos() + 118f, 8, 28, true);
        createLadder(screen.getScreenXPos() + 32f, screen.getScreenYPos() + 120f, 8, 24, true);
    }

    private void createSixthPlatform() {
        float bottomY = (float) screen.getScreenYPos() + getScreenHeight() - PLATFORM_HEIGHT;

        createPlatformLine(screen.getScreenXPos(), bottomY - 164f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 8, 1, 0);
        createPlatformLine(screen.getScreenXPos() + (8f * PLATFORM_WIDTH), bottomY - 164f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 5, 1, 1f);

        createLadder(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 84f, 8, 12, true);
        createLadder(screen.getScreenXPos() + 184f, screen.getScreenYPos() + 87f, 8, 24, true);
    }

    private void createFinalPlatform() {
        createPlatformLine(screen.getScreenXPos() + 88f, screen.getScreenYPos() + 56f, PLATFORM_WIDTH, PLATFORM_HEIGHT, 3, 1, 0);
        createLadder(screen.getScreenXPos() + 128f, screen.getScreenYPos() + 56f, 8, 28, false);

        this.getPlatforms().add(new Platform(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 24f, 8, 8));
        this.getPlatforms().add(new Platform(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 24f, 8, 8));
        createLadder(screen.getScreenXPos() + 64f, screen.getScreenYPos() + 24f, 8, 60, false);
        createLadder(screen.getScreenXPos() + 80f, screen.getScreenYPos() + 24f, 8, 60, false);
    }
}
