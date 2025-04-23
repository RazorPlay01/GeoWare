package com.github.razorplay01.geowaremod.games.arkanoid;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.Ball;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.Player;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup.MultiBallPowerUp;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup.PowerUp;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup.WidthDecreasePowerUp;
import com.github.razorplay01.geowaremod.games.arkanoid.entity.powerup.WidthIncreasePowerUp;
import com.github.razorplay01.geowaremod.games.arkanoid.mapobject.Brick;
import com.github.razorplay01.geowaremod.games.arkanoid.mapobject.BrickColor;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
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

    private static final float POWERUP_SPAWN_CHANCE = 0.20f;

    public final List<Brick> bricks = new ArrayList<>();
    public Player player;
    public List<Ball> balls = new ArrayList<>();
    public List<PowerUp> powerUps = new ArrayList<>();

    private final int level;
    private final CollisionHandler collisionHandler;

    private static final Map<Character, BrickColor> BRICK_MAPPING = Map.of(
            'Y', BrickColor.YELLOW,
            'P', BrickColor.PURPLE,
            'W', BrickColor.WHITE,
            'R', BrickColor.RED,
            'G', BrickColor.GREEN,
            'B', BrickColor.BLUE
    );

    public ArkanoidGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int level) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.level = level;
        this.collisionHandler = new CollisionHandler(this);
    }

    @Override
    public void init() {
        super.init();

        if (bricks.isEmpty() || player == null) {
            createGameMap();
        }

        int paddleY = screen.getGameScreenYPos() + (ROWS * BRICK_HEIGHT) + EXTRA_SPACE;
        float paddleWidth = 32f; // Ancho inicial del paddle
        int paddleX = screen.getGameScreenXPos() + (GAME_WIDTH / 2) - (int) (paddleWidth / 2); // Centrar el paddle

        this.player = new Player(paddleX, paddleY, paddleWidth, PADDLE_HEIGHT, screen);
        Ball ball = new Ball(
                player.getXPos() + player.getWidth() / 2, // Centrar la bola respecto al paddle
                player.getYPos() - 4,
                4,
                screen
        );
        ball.setMoving(false);
        balls.add(ball);
    }

    @Override
    public void update() {
        super.update();

        if (initDelay.isFinished() && !balls.isEmpty()) {
            balls.getFirst().setMoving(true);
        }

        if (status == GameStatus.ACTIVE && (player.isLosing() || player.isWinning())) {
            status = GameStatus.ENDING;
        }
        if (status == GameStatus.ENDING && !balls.isEmpty()) {
            balls.forEach(ball -> ball.setMoving(false));
        }

        player.update();

        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            powerUp.update();
            if (!powerUp.isActive()) {
                powerUpIterator.remove();
            }
        }

        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball ball = ballIterator.next();
            ball.update();
            if (!ball.isActive()) {
                ballIterator.remove();
            }
        }

        if (status == GameStatus.ACTIVE && balls.isEmpty()) {
            player.setLosing(true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        powerUps.forEach(powerUp -> powerUp.render(context, delta));
        bricks.forEach(platform -> platform.render(context));
        balls.forEach(ball -> ball.render(context, delta));
        player.render(context, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier backgroundTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/arkanoid/fondo.png");

        context.drawTexture(backgroundTexture, screen.getGameScreenXPos() - 12, screen.getGameScreenYPos() - 8,
                getScreenWidth() + 24, getScreenHeight() + 16, 0, 0, 416, 464, 416, 464);
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
        if (status != GameStatus.ACTIVE) return;
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == client.options.leftKey.getDefaultKey().getCode()) {
            player.moveLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == client.options.rightKey.getDefaultKey().getCode()) {
            player.moveRight();
        }
    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {
        if (status != GameStatus.ACTIVE) return;
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            player.stopMovingLeft();
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            player.stopMovingRight();
        }
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

            if (Math.random() < 0.33) {
                powerUp = new MultiBallPowerUp(powerUpX, powerUpY, (ArkanoidGameScreen) screen);
            } else if (Math.random() < 0.66) {
                powerUp = new WidthIncreasePowerUp(powerUpX, powerUpY, (ArkanoidGameScreen) screen);
            } else {
                powerUp = new WidthDecreasePowerUp(powerUpX, powerUpY, (ArkanoidGameScreen) screen);
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
            "---RRRRRRR---",
            "--GGGGGGGGG--",
            "-BBBBBBBBBBB-",
            "RRRRRRRRRRRRR",
            "GGGGGGGGGGGGG",
            "RRRRRRRRRRRRR",
            "-BBBBBBBBBBB-",
            "--GGGGGGGGG--",
            "---RRRRRRR---",
            "-------------",
            "-------------"
    };
    private static final String[] LEVEL_2 = {
            "-------------",
            "-------------",
            "---B-----B---",
            "---B-----B---",
            "----B---B----",
            "----B---B----",
            "---PPPPPPP---",
            "---PPPPPPP---",
            "--PPPPPPPPP--",
            "--PPPPPPPPP--",
            "-PPPPPPPPPPP-",
            "-PPPPPPPPPPP-",
            "-PPPPPPPPPPP-",
            "-P-PPPPPPP-P-",
            "-P-P-----P-P-",
            "-P-P-----P-P-",
            "----PP-PP----",
            "----PP-PP----",
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
                if (brickChar != '-') {
                    BrickColor color = BRICK_MAPPING.get(brickChar);
                    if (color != null) {
                        addBrick(row, col, color);
                    }
                }
            }
        }
    }
}