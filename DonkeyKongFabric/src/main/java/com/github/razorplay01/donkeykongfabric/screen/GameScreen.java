package com.github.razorplay01.donkeykongfabric.screen;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.Barrel;
import com.github.razorplay01.donkeykongfabric.game.Ladder;
import com.github.razorplay01.donkeykongfabric.game.Platform;
import com.github.razorplay01.donkeykongfabric.game.Player;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreen extends Screen {
    public static final int SCREEN_WIDTH = 224;
    public static final int SCREEN_HEIGHT = 256;
    private int screenXpos;
    private int screenYpos;

    private Player player;
    @Getter
    private final List<Barrel> barrels = new ArrayList<>();
    @Getter
    private final List<Ladder> ladders = new ArrayList<>();
    @Getter
    private final List<Platform> platforms = new ArrayList<>();

    private Identifier backgroundImage = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/map_base.png");

    private TextRenderer textRenderer;
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
        textRenderer = client.textRenderer;
        this.screenXpos = (width / 2) - (SCREEN_WIDTH / 2);
        this.screenYpos = (height / 2) - (SCREEN_HEIGHT / 2);

        float centerX = screenXpos + ((float) SCREEN_WIDTH / 2) - 4f;
        float bottomY = screenYpos + SCREEN_HEIGHT - 8f;

        createFirstPlatform(bottomY);
        createSecondPlatform(bottomY);
        createThirdPlatform(bottomY);
        createFourthPlatform(bottomY);
        createFifthPlatform(bottomY);
        createSixthPlatform(bottomY);
        player = new Player(platforms.get(4).getX(), platforms.get(4).getY() - 16, this);
    }

    private void createFirstPlatform(float bottomY) {
        for (int i = 0; i < 14; i++) {
            float platformX = screenXpos + (i * 8f);
            platforms.add(new Platform(platformX, bottomY));
        }
        float currentX = screenXpos + (14 * 8f);
        float currentY = bottomY - 1;
        for (int i = 0; i < 7; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX + 8f, currentY));
            currentX += 16;
            currentY -= 1f;
        }
        ladders.add(new Ladder(platforms.get(10).getX(), platforms.get(10).getY() - 8, 8, false));
    }

    private void createSecondPlatform(float bottomY) {
        float currentX = screenXpos;
        float currentY = bottomY - 40;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX + 8f, currentY));
            currentX += 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(platforms.get(38).getX(), platforms.get(38).getY(), 11, true));
        ladders.add(new Ladder(platforms.get(51).getX(), platforms.get(51).getY(), 24, true));
    }

    private void createThirdPlatform(float bottomY) {
        float currentX = screenXpos + SCREEN_WIDTH - 8f;
        float currentY = bottomY - 73f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX - 8f, currentY));
            currentX -= 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(platforms.get(69).getX(), platforms.get(69).getY(), 32, true));
        ladders.add(new Ladder(platforms.get(73).getX(), platforms.get(73).getY() - 8, 8, false));
        ladders.add(new Ladder(platforms.get(77).getX(), platforms.get(77).getY(), 24, true));

    }

    private void createFourthPlatform(float bottomY) {
        float currentX = screenXpos;
        float currentY = bottomY - 106f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX + 8f, currentY));
            currentX += 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(platforms.get(88).getX(), platforms.get(88).getY(), 14, true));
        ladders.add(new Ladder(platforms.get(94).getX(), platforms.get(94).getY(), 32, true));
        ladders.add(new Ladder(platforms.get(101).getX(), platforms.get(101).getY() - 8, 8, false));
        ladders.add(new Ladder(platforms.get(103).getX(), platforms.get(103).getY(), 24, true));
    }

    private void createFifthPlatform(float bottomY) {
        float currentX = screenXpos + SCREEN_WIDTH - 8f;
        float currentY = bottomY - 139f;
        for (int i = 0; i < 13; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX - 8f, currentY));
            currentX -= 16;
            currentY += 1f;
        }
        ladders.add(new Ladder(platforms.get(112).getX(), platforms.get(112).getY(), 16, true));
        ladders.add(new Ladder(platforms.get(122).getX(), platforms.get(122).getY() - 13, 13, false));
        ladders.add(new Ladder(platforms.get(124).getX(), platforms.get(124).getY(), 28, true));
        ladders.add(new Ladder(platforms.get(129).getX(), platforms.get(129).getY(), 24, true));
    }

    private void createSixthPlatform(float bottomY) {
        float currentY = bottomY - 164f;
        for (int i = 0; i < 16; i++) {
            float platformX = screenXpos + (i * 8f);
            platforms.add(new Platform(platformX, currentY));
        }

        float currentX = screenXpos + (16 * 8f);
        for (int i = 0; i < 5; i++) {
            platforms.add(new Platform(currentX, currentY));
            platforms.add(new Platform(currentX + 8f, currentY));

            currentX += 16f;
            currentY += 1f;
        }
        ladders.add(new Ladder(platforms.get(143).getX(), platforms.get(143).getY(), 12, true));
        ladders.add(new Ladder(platforms.get(148).getX(), platforms.get(148).getY() - 28, 28, false));
        ladders.add(new Ladder(platforms.get(155).getX(), platforms.get(155).getY(), 24, true));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(
                backgroundImage,
                screenXpos,
                screenYpos,
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
            // Actualizar barril y verificar si debe eliminarse
            if (barrel.update(screenXpos, SCREEN_WIDTH, platforms, player, ladders)) {
                barrelsToRemove.add(barrel); // Añadir a la lista para eliminar
            }
            barrel.checkCollision(player);
            barrel.render(context);
        }

        // Eliminar los barriles marcados
        barrels.removeAll(barrelsToRemove);

        player.update(screenXpos, screenYpos, SCREEN_WIDTH, SCREEN_HEIGHT, platforms);
        player.render(context);

        if (showJumpMessage) {
            int textWidth = textRenderer.getWidth("Presiona: " + Text.translatable(client.options.jumpKey.getBoundKeyTranslationKey()).getString());
            context.drawText(textRenderer, "Presiona: " + Text.translatable(client.options.jumpKey.getBoundKeyTranslationKey()).getString(), screenXpos + (SCREEN_WIDTH - textWidth) / 2, screenYpos + SCREEN_HEIGHT - 20, 0xFFFFFF, false);
        }

        player.checkBarrelJump(barrels);

        // Renderizar la puntuación
        String scoreText = String.format("Score: %d", player.getScore());
        context.drawText(
                textRenderer,
                scoreText,
                screenXpos + 10, // 10 píxeles desde la izquierda
                screenYpos + 40, // 10 píxeles desde arriba
                0xFFFFFF, // Color blanco
                true
        );

        int seconds = timeRemaining / 20; // Convertir ticks a segundos
        String timeText = String.format("%d00", seconds);
        context.drawText(
                textRenderer,
                timeText,
                screenXpos + SCREEN_WIDTH - textRenderer.getWidth(timeText) - 10,
                screenYpos + 40,
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
            if (random.nextFloat() < BARREL_SPAWN_PROBABILITY) {
                // Crear un nuevo barril en la posición inicial
                // Usando la posición del primer barril como referencia
                barrels.add(new Barrel(platforms.get(137).getX(), platforms.get(137).getY() - 30f));
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