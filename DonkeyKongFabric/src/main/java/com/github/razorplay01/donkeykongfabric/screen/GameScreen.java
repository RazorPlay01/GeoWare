package com.github.razorplay01.donkeykongfabric.screen;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.Barrel;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.Player;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.razorplay01.donkeykongfabric.game.Player.PLAYER_HEIGHT;

public class GameScreen extends Screen {
    public static final int SCREEN_WIDTH = 224;
    public static final int SCREEN_HEIGHT = 256;
    @Getter
    private Integer screenXPos;
    @Getter
    private Integer screenYPos;

    private Player player;
    @Getter
    private final List<Barrel> barrels = new ArrayList<>();
    @Getter
    private final List<Ladder> ladders = new ArrayList<>();
    @Getter
    private final List<Platform> platforms = new ArrayList<>();

    private final Identifier backgroundImage = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/map_base.png");

    @Setter
    private boolean showJumpMessage = false;

    private int timeRemaining = 70 * 20; // 70 segundos * 20 ticks por segundo
    private int barrelSpawnTimer = 0;
    private static final int BARREL_SPAWN_INTERVAL = 80; // Intervalo de spawn en ticks
    private static final float BARREL_SPAWN_PROBABILITY = 0.7f; // 70% de probabilidad
    private final Random random = new Random();

    public GameScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        if (screenXPos == null || screenYPos == null || platforms.isEmpty() || player == null) {
            this.screenXPos = (width / 2) - (SCREEN_WIDTH / 2);
            this.screenYPos = (height / 2) - (SCREEN_HEIGHT / 2);

            createFirstPlatform();
            createSecondPlatform();
            createThirdPlatform();
            createFourthPlatform();
            createFifthPlatform();
            createSixthPlatform();
            createFinalPlatform();
            player = new Player(screenXPos + 36f, platforms.getFirst().getYPos() - PLAYER_HEIGHT, this);
        }
    }

    private void createFirstPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        for (int i = 0; i < 7; i++) {
            float platformX = screenXPos + ((float) i * platformWidth);
            platforms.add(new Platform(platformX, bottomY, platformWidth, platformHeight));
        }
        float currentX = screenXPos + ((float) 7 * platformWidth);
        float currentY = bottomY - 1;
        for (int i = 0; i < 7; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX += 16;
            currentY -= 1f;
        }
        ladders.add(new Ladder(screenXPos + 80f, screenYPos + 240f, 8, 8, false));
    }

    private void createSecondPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        float currentX = screenXPos;
        float currentY = bottomY - 40;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX += 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(screenXPos + 80f, screenYPos + 213f, 8, 11, true));
        ladders.add(new Ladder(screenXPos + 184f, screenYPos + 219f, 8, 24, true));
    }

    private void createThirdPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        float currentX = (float) screenXPos + SCREEN_WIDTH - platformWidth;
        float currentY = bottomY - 73f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX -= 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(screenXPos + 96f, screenYPos + 182f, 8, 32, true));
        ladders.add(new Ladder(screenXPos + 64f, screenYPos + 176f, 8, 8, false));
        ladders.add(new Ladder(screenXPos + 32f, screenYPos + 186f, 8, 24, true));

    }

    private void createFourthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        float currentX = screenXPos;
        float currentY = bottomY - 106f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX += 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(screenXPos + 64f, screenYPos + 146f, 8, 14, true));
        ladders.add(new Ladder(screenXPos + 112f, screenYPos + 149f, 8, 32, true));
        ladders.add(new Ladder(screenXPos + 168f, screenYPos + 144f, 8, 8, false));
        ladders.add(new Ladder(screenXPos + 184f, screenYPos + 153f, 8, 24, true));
    }

    private void createFifthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        float currentX = (float) screenXPos + SCREEN_WIDTH - platformWidth;
        float currentY = bottomY - 139f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));
            currentX -= 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(screenXPos + 168f, screenYPos + 112f, 8, 16, true));
        ladders.add(new Ladder(screenXPos + 88f, screenYPos + 104f, 8, 13, false));
        ladders.add(new Ladder(screenXPos + 72f, screenYPos + 118f, 8, 28, true));
        ladders.add(new Ladder(screenXPos + 32f, screenYPos + 120f, 8, 24, true));
    }

    private void createSixthPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float bottomY = (float) screenYPos + SCREEN_HEIGHT - platformHeight;

        float currentY = bottomY - 164f;
        for (int i = 0; i < 8; i++) {
            float platformX = (float) screenXPos + (i * platformWidth);
            platforms.add(new Platform(platformX, currentY, platformWidth, platformHeight));
        }

        float currentX = screenXPos + (8f * platformWidth);
        for (int i = 0; i < 5; i++) {
            platforms.add(new Platform(currentX, currentY, platformWidth, platformHeight));

            currentX += 16f;
            currentY += 1f;
        }
        ladders.add(new Ladder(screenXPos + 88f, screenYPos + 84f, 8, 12, true));
        ladders.add(new Ladder(screenXPos + 184f, screenYPos + 87f, 8, 24, true));
    }

    private void createFinalPlatform() {
        int platformWidth = 16;
        int platformHeight = 8;
        float currentX = screenXPos + 88f;
        float currentY = screenYPos + 56f;
        for (int i = 0; i < 3; i++) {
            float platformX = currentX + (i * platformWidth);
            platforms.add(new Platform(platformX, currentY, platformWidth, platformHeight));
        }

        ladders.add(new Ladder(screenXPos + 128f, screenYPos + 56f, 8, 28, false));
        platforms.add(new Platform(screenXPos + 64f, screenYPos + 24f, 8, 8));
        platforms.add(new Platform(screenXPos + 80f, screenYPos + 24f, 8, 8));
        ladders.add(new Ladder(screenXPos + 64f, screenYPos + 24f, 8, 60, false));
        ladders.add(new Ladder(screenXPos + 80f, screenYPos + 24f, 8, 60, false));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(
                backgroundImage,
                screenXPos,
                screenYPos,
                0,
                0,
                SCREEN_WIDTH,
                SCREEN_HEIGHT,
                SCREEN_WIDTH,
                SCREEN_HEIGHT
        );

        for (Platform platform : platforms) {
            platform.render(context);
        }
        for (Ladder ladder : ladders) {
            ladder.render(context);
        }

        // Lista temporal para almacenar los barriles que deben eliminarse
        List<Barrel> barrelsToRemove = new ArrayList<>();
        for (Barrel barrel : barrels) {
            // Actualizar barril yPos verificar si debe eliminarse
            if (barrel.update(screenXPos, SCREEN_WIDTH, platforms, player, ladders)) {
                barrelsToRemove.add(barrel); // Añadir a la lista para eliminar
            }
            barrel.checkCollision(player);
            barrel.render(context);
        }

        // Eliminar los barriles marcados
        barrels.removeAll(barrelsToRemove);

        player.update(screenXPos, screenYPos, SCREEN_WIDTH, SCREEN_HEIGHT, platforms);
        player.render(context);

        if (showJumpMessage) {
            int textWidth = textRenderer.getWidth("Presiona: " + Text.translatable(client.options.jumpKey.getBoundKeyTranslationKey()).getString());
            context.drawText(textRenderer, "Presiona: " + Text.translatable(client.options.jumpKey.getBoundKeyTranslationKey()).getString(), screenXPos + (SCREEN_WIDTH - textWidth) / 2, screenYPos + SCREEN_HEIGHT - 20, 0xFFFFFF, false);
        }

        player.checkBarrelJump(barrels);

        // Renderizar la puntuación
        String scoreText = String.format("Score: %d", player.getScore());
        context.drawText(
                textRenderer,
                scoreText,
                screenXPos + 10, // 10 píxeles desde la izquierda
                screenYPos + 40, // 10 píxeles desde arriba
                0xFFFFFF, // Color blanco
                true
        );

        int seconds = timeRemaining / 20; // Convertir ticks a segundos
        String timeText = String.format("%d00", seconds);
        context.drawText(
                textRenderer,
                timeText,
                screenXPos + SCREEN_WIDTH - textRenderer.getWidth(timeText) - 10,
                screenYPos + 40,
                0xFFFFFF, // Color blanco
                true
        );
        updateTimeAndBarrels();
    }

    private void updateTimeAndBarrels() {
        // Actualizar el tiempo restante
        if (timeRemaining > 0) {
            timeRemaining--;
        }

        // Actualizar el temporizador de spawn de barriles
        barrelSpawnTimer++;
        if (barrelSpawnTimer >= BARREL_SPAWN_INTERVAL) {
            barrelSpawnTimer = 0;

            // Verificar la probabilidad de spawn
            if (random.nextFloat() < BARREL_SPAWN_PROBABILITY && !player.isWinning()) {
                // Crear un nuevo barril en la posición inicial
                // Usando la posición del primer barril como referencia
                //45 70
                barrels.add(new Barrel(screenXPos + 45f, screenYPos + 71f));
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            player.moveLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            player.moveRight();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            player.moveUp(ladders);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            player.moveDown(ladders);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_SPACE) {
            player.jump(SCREEN_HEIGHT);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            player.stopMovingLeft();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            player.stopMovingRight();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            player.stopMovingUp();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            player.stopMovingDown();
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}