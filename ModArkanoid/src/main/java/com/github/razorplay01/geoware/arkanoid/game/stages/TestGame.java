package com.github.razorplay01.geoware.arkanoid.game.stages;

import com.github.razorplay01.geoware.arkanoid.game.entity.Ball;
import com.github.razorplay01.geoware.arkanoid.game.entity.MultiBallPowerUp;
import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.game.entity.PowerUp;
import com.github.razorplay01.geoware.arkanoid.game.mapobject.Brick;
import com.github.razorplay01.geoware.arkanoid.game.util.BrickColor;
import com.github.razorplay01.geoware.arkanoid.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;

public class TestGame extends Game {
    private static final int BRICK_WIDTH = 16;
    private static final int BRICK_HEIGHT = 8;
    private static final int COLUMNS = 13;
    private static final int ROWS = 18;
    private static final int PADDLE_HEIGHT = 8;
    private static final int BOTTOM_SPACE = 16;
    private static final int EXTRA_SPACE = 64;

    // Dimensiones totales del área de juego
    private static final int GAME_WIDTH = BRICK_WIDTH * COLUMNS;
    private static final int GAME_HEIGHT = (ROWS * BRICK_HEIGHT) + EXTRA_SPACE + PADDLE_HEIGHT + BOTTOM_SPACE;

    private static final float POWERUP_SPAWN_CHANCE = 0.2f; // 20% de probabilidad

    public TestGame(GameScreen screen) {
        super(null, screen);
    }

    @Override
    public void init() {
        if (this.getBricks().isEmpty() || this.getPlayer() == null) {
            createGameMap();
        }
        int paddleY = screen.getScreenYPos() + (ROWS * BRICK_HEIGHT) + EXTRA_SPACE;
        int paddleX = screen.getScreenXPos() + GAME_WIDTH / 2;

        this.player = new Player(paddleX, paddleY, 32, 8, screen);
        // Inicializar la pelota encima del jugador
        Ball initialBall = new Ball(
                player.getXPos() + player.getWidth() / 2,
                player.getYPos() - 8,
                8,
                8,
                screen
        );
        balls.add(initialBall);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Renderizar ladrillos
        getBricks().forEach(platform -> platform.render(context));

        // Renderizar score
        String scoreText = "Score: " + player.getScore();
        context.drawText(
                client.textRenderer,
                scoreText,
                screen.getScreenXPos() + GAME_WIDTH + 10, // Posición X
                screen.getScreenYPos(), // Posición Y
                0xFFFFFFFF, // Color blanco
                true // Con sombra
        );
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dibujar el fondo del área de juego
        context.fill(screen.getScreenXPos(), screen.getScreenYPos(), screen.getScreenXPos() + GAME_WIDTH, screen.getScreenYPos() + GAME_HEIGHT, 0xFF000000);

        // Dibujar el borde del área de juego
        context.drawBorder(screen.getScreenXPos() - 1, screen.getScreenYPos() - 1, GAME_WIDTH + 2, GAME_HEIGHT + 2, 0xFFFFFFFF);
    }

    @Override
    public void updateAndRenderPlayer(DrawContext context, int mouseX, int mouseY, float delta) {
        for (PowerUp powerUp : new ArrayList<>(powerUps)) {
            powerUp.update();
            powerUp.render(context);
        }

        // Actualizar y renderizar jugador
        player.update();
        player.render(context);

        // Actualizar y renderizar bolas
        for (Ball ball : new ArrayList<>(balls)) {
            ball.update();
            ball.render(context);
        }
    }

    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    @Override
    public void createGameMap() {
        String[] level = LEVEL_2;

        for (int row = 0; row < level.length; row++) {
            String rowStr = level[row];
            for (int col = 0; col < rowStr.length(); col++) {
                char brickChar = rowStr.charAt(col);
                if (brickChar != '-') {  // Ignorar los guiones
                    BrickColor color = BRICK_MAPPING.get(brickChar);
                    if (color != null) {
                        addBrick(row, col, color);
                    }
                }
            }
        }
    }

    @Override
    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    @Override
    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    public void addBrick(int row, int col, BrickColor color) {
        float x = ((float) screen.getScreenXPos() + (col * BRICK_WIDTH));
        float y = ((float) screen.getScreenYPos() + (row * BRICK_HEIGHT));
        getBricks().add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, color));
    }

    public void removeBrick(Brick brick) {
        getBricks().remove(brick);

        // Verificar si quedan ladrillos
        if (getBricks().isEmpty()) {
            player.setWinning(true);
        }

        // El resto del código existente para el powerUp
        if (Math.random() < POWERUP_SPAWN_CHANCE) {
            PowerUp powerUp;
            float powerUpX = brick.getXPos() + (brick.getWidth() - 12) / 2;
            float powerUpY = brick.getYPos() + (brick.getHeight() - 12) / 2;

            powerUp = new MultiBallPowerUp(powerUpX, powerUpY, screen);
            powerUps.add(powerUp);
        }
    }

    public void addBall(Ball ball) {
        balls.add(ball);
    }

    public void removeBall(Ball ball) {
        balls.remove(ball);

        // Verificar si era la última bola
        boolean hasActiveBalls = false;
        for (Ball remainingBall : balls) {
            if (remainingBall.isMoving()) {
                hasActiveBalls = true;
                break;
            }
        }

        // Si no quedan bolas activas, el jugador pierde
        if (!hasActiveBalls) {
            player.setLosing(true);
        }
    }

    private static final String[] LEVEL_1 = {
            "-------------",
            "-------------",
            "---YYYYYYY---",
            "--RRRRRRRRR--",
            "-BBBBBBBBBBB-",
            "MMMMMMMMMMMMM",
            "GGGGGGGGGGGGG",
            "MMMMMMMMMMMMM",
            "-BBBBBBBBBBB-",
            "--RRRRRRRRR--",
            "---YYYYYYY---",
            "-------------",
            "-------------"
    };
    private static final String[] LEVEL_2 = {
            "-------------",
            "-------------",
            "---Y-----Y---",
            "---Y-----Y---",
            "----Y---Y----",
            "----Y---Y----",
            "---SSSSSSS---",
            "---SSSSSSS---",
            "--SSRSSSRSS--",
            "--SSRSSSRSS--",
            "-SSSSSSSSSSS-",
            "-SSSSSSSSSSS-",
            "-SSSSSSSSSSS-",
            "-S-SSSSSSS-S-",
            "-S-S-----S-S-",
            "-S-S-----S-S-",
            "----SS-SS----",
            "----SS-SS----",
    };
}
