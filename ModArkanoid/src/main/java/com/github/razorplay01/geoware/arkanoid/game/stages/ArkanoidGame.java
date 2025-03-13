package com.github.razorplay01.geoware.arkanoid.game.stages;

import com.github.razorplay01.geoware.arkanoid.game.entity.Ball;
import com.github.razorplay01.geoware.arkanoid.game.entity.powerup.MultiBallPowerUp;
import com.github.razorplay01.geoware.arkanoid.game.entity.Player;
import com.github.razorplay01.geoware.arkanoid.game.entity.powerup.PowerUp;
import com.github.razorplay01.geoware.arkanoid.game.entity.powerup.WidthDecreasePowerUp;
import com.github.razorplay01.geoware.arkanoid.game.entity.powerup.WidthIncreasePowerUp;
import com.github.razorplay01.geoware.arkanoid.game.mapobject.Brick;
import com.github.razorplay01.geoware.arkanoid.game.util.BrickColor;
import com.github.razorplay01.geoware.arkanoid.game.util.game.Game;
import com.github.razorplay01.geoware.arkanoid.game.util.game.GameStatus;
import com.github.razorplay01.geoware.arkanoid.game.util.render.CustomDrawContext;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Getter
public class ArkanoidGame extends Game {
    private static final int BRICK_WIDTH = 16;
    private static final int BRICK_HEIGHT = 8;
    private static final int COLUMNS = 13;
    private static final int ROWS = 18;
    private static final int PADDLE_HEIGHT = 8;
    private static final int BOTTOM_SPACE = 16;
    private static final int EXTRA_SPACE = 64;

    private static final int GAME_WIDTH = BRICK_WIDTH * COLUMNS;
    private static final int GAME_HEIGHT = (ROWS * BRICK_HEIGHT) + EXTRA_SPACE + PADDLE_HEIGHT + BOTTOM_SPACE;

    private static final float POWERUP_SPAWN_CHANCE = 0.2f; // 20% de probabilidad

    public final List<Brick> bricks = new ArrayList<>();
    public Player player;
    public List<Ball> balls = new ArrayList<>();
    public List<PowerUp> powerUps = new ArrayList<>();

    private final int level;

    private static final Map<Character, BrickColor> BRICK_MAPPING = Map.of(
            'R', BrickColor.RED,
            'G', BrickColor.GREEN,
            'B', BrickColor.BLUE,
            'Y', BrickColor.YELLOW,
            'M', BrickColor.MAGENTA,
            'S', BrickColor.GRAY,
            'C', BrickColor.CYAN
    );

    public ArkanoidGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int level) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.level = level;
    }

    @Override
    public void init() {
        super.init();

        if (bricks.isEmpty() || player == null) {
            createGameMap();
        }

        int paddleY = screen.getGameScreenYPos() + (ROWS * BRICK_HEIGHT) + EXTRA_SPACE;
        int paddleX = screen.getGameScreenXPos() + GAME_WIDTH / 2;

        this.player = new Player(paddleX, paddleY, 32, 8, screen);
        Ball ball = new Ball(
                player.getXPos() + player.getWidth() / 2,
                player.getYPos() - 8,
                8,
                8,
                screen
        );
        //ball.setMoving(false);
        balls.add(ball);
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE && (player.isLosing() || player.isWinning())) {
            status = GameStatus.ENDING;
        }
        if (status == GameStatus.ENDING && !balls.isEmpty()) {
            balls.forEach(ball -> ball.setMoving(false));
        }

        player.update();
        for (PowerUp powerUp : new ArrayList<>(powerUps)) {
            powerUp.update();
        }


        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.update(); // Actualizar posición, colisiones, etc.
            if (!ball.isActive()) { // Lógica para determinar si la bola debe eliminarse
                iterator.remove(); // Eliminar la bola de forma segura
            }
        }

        // Verificar si hay bolas activas después de eliminar
        boolean hasActiveBalls = balls.stream().anyMatch(Ball::isMoving);
        if (!hasActiveBalls) {
            player.setLosing(true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        powerUps.forEach(powerUp -> powerUp.render(context));
        bricks.forEach(platform -> platform.render(context));
        balls.forEach(ball -> ball.render(context));
        player.render(context);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        customDrawContext.drawBasicBackground(this.screen);
    }

    @Override
    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    @Override
    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (status != GameStatus.ACTIVE) {
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
            player.moveLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
            player.moveRight();
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (status != GameStatus.ACTIVE) {
            return;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            player.stopMovingLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            player.stopMovingRight();
        }
    }

    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    public void addBrick(int row, int col, BrickColor color) {
        float x = ((float) screen.getGameScreenXPos() + (col * BRICK_WIDTH));
        float y = ((float) screen.getGameScreenYPos() + (row * BRICK_HEIGHT));
        bricks.add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, color));
    }

    public void removeBrick(Brick brick) {
        bricks.remove(brick);

        if (bricks.isEmpty()) {
            player.setWinning(true);
        }

        if (Math.random() < POWERUP_SPAWN_CHANCE) {
            PowerUp powerUp;
            float powerUpX = brick.getXPos() + (brick.getWidth() - 12) / 2;
            float powerUpY = brick.getYPos() + (brick.getHeight() - 12) / 2;

            // Elegir aleatoriamente entre los diferentes power-ups
            if (Math.random() < 0.33) {
                powerUp = new MultiBallPowerUp(powerUpX, powerUpY, screen);
            } else if (Math.random() < 0.66) {
                powerUp = new WidthIncreasePowerUp(powerUpX, powerUpY, screen);
            } else {
                powerUp = new WidthDecreasePowerUp(powerUpX, powerUpY, screen);
            }

            powerUps.add(powerUp);
        }
    }

    public void addBall(Ball ball) {
        balls.add(ball);
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
    private static final String[] LEVEL_3 = {
            "------R------",
            "-----RRR-----",
            "----RRRRR----",
            "---RRRRRRR---",
            "--RRRR-RRRR--",
            "-RRRR---RRRR-",
            "RRRR-----RRRR",
            "RRR---R---RRR",
            "RR----R----RR",
            "R-----R-----R",
            "------R------",
            "------R------",
            "------R------"
    };

    public void createGameMap() {
        String[] level;
        switch (this.level) {
            case 2 -> level = LEVEL_2;
            case 3 -> level = LEVEL_3;
            default -> level = LEVEL_1;

        }
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
}
