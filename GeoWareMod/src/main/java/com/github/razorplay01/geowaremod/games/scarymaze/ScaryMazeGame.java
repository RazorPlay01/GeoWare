package com.github.razorplay01.geowaremod.games.scarymaze;

import com.github.razorplay01.geowaremod.GeoWareMod;
import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ScaryMazeGame extends Game {
    private final int level;
    private final List<RectangleHitbox> walls = new ArrayList<>();
    private RectangleHitbox finalArea;
    private RectangleHitbox player;
    private boolean isPlayerBoundToMouse = false;
    private final float scaleFactor = 2.0f; // Factor de escala (ajústalo según necesites)
    private final float soundVolume = 0.3f;
    private final int finalPoints;

    protected ScaryMazeGame(Screen screen, int initDelay, int timeLimitSeconds, int prevScore, int level, int finalPoints) {
        super(screen, initDelay, timeLimitSeconds, prevScore);
        this.finalPoints = finalPoints;
        this.level = Math.max(1, Math.min(level, 3));
    }

    @Override
    public void init() {
        super.init();
        int xPos = screen.getGameScreenXPos();
        int yPos = screen.getGameScreenYPos();
        switch (level) {
            case 1:
                walls.add(new RectangleHitbox("wall1", xPos + 0 * scaleFactor, yPos + 0 * scaleFactor, 208 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall2", xPos + 0 * scaleFactor, yPos + 32 * scaleFactor, 176 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall3", xPos + 0 * scaleFactor, yPos + 16 * scaleFactor, 16 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall4", xPos + 0 * scaleFactor, yPos + 48 * scaleFactor, 32 * scaleFactor, 64 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall5", xPos + 32 * scaleFactor, yPos + 96 * scaleFactor, 112 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall6", xPos + 64 * scaleFactor, yPos + 64 * scaleFactor, 144 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall7", xPos + 192 * scaleFactor, yPos + 16 * scaleFactor, 16 * scaleFactor, 48 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall8", xPos + 160 * scaleFactor, yPos + 80 * scaleFactor, 48 * scaleFactor, 32 * scaleFactor, 0, 0, 0xAAFF0000));
                this.player = new RectangleHitbox("player", xPos + 23 * scaleFactor, yPos + 23 * scaleFactor, 3 * scaleFactor, 3 * scaleFactor, 0, 0, 0xFFFF0000);
                this.finalArea = new RectangleHitbox("finalArea", xPos + 145 * scaleFactor, yPos + 97 * scaleFactor, 14 * scaleFactor, 14 * scaleFactor, 0, 0, 0xAA00FF00);
                break;
            case 2:
                walls.add(new RectangleHitbox("wall1", xPos + 0 * scaleFactor, yPos + 0 * scaleFactor, 16 * scaleFactor, 112 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall2", xPos + 32 * scaleFactor, yPos + 0 * scaleFactor, 32 * scaleFactor, 34 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall3", xPos + 16 * scaleFactor, yPos + 46 * scaleFactor, 80 * scaleFactor, 20 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall4", xPos + 80 * scaleFactor, yPos + 30 * scaleFactor, 16 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall5", xPos + 16 * scaleFactor, yPos + 66 * scaleFactor, 48 * scaleFactor, 2 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall6", xPos + 16 * scaleFactor, yPos + 68 * scaleFactor, 16 * scaleFactor, 44 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall7", xPos + 32 * scaleFactor, yPos + 92 * scaleFactor, 176 * scaleFactor, 20 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall8", xPos + 64 * scaleFactor, yPos + 0 * scaleFactor, 144 * scaleFactor, 18 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall9", xPos + 112 * scaleFactor, yPos + 18 * scaleFactor, 16 * scaleFactor, 66 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall10", xPos + 48 * scaleFactor, yPos + 76 * scaleFactor, 16 * scaleFactor, 8 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall11", xPos + 64 * scaleFactor, yPos + 78 * scaleFactor, 48 * scaleFactor, 6 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall12", xPos + 144 * scaleFactor, yPos + 28 * scaleFactor, 32 * scaleFactor, 24 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall13", xPos + 144 * scaleFactor, yPos + 52 * scaleFactor, 16 * scaleFactor, 40 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall14", xPos + 128 * scaleFactor, yPos + 18 * scaleFactor, 80 * scaleFactor, 2 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall15", xPos + 192 * scaleFactor, yPos + 20 * scaleFactor, 16 * scaleFactor, 64 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall16", xPos + 176 * scaleFactor, yPos + 60 * scaleFactor, 16 * scaleFactor, 24 * scaleFactor, 0, 0, 0xAAFF0000));
                this.player = new RectangleHitbox("player", xPos + 23 * scaleFactor, yPos + 5 * scaleFactor, 2 * scaleFactor, 2 * scaleFactor, 0, 0, 0xFFFF0000);
                this.finalArea = new RectangleHitbox("finalArea", xPos + 195 * scaleFactor, yPos + 84 * scaleFactor, 13 * scaleFactor, 8 * scaleFactor, 0, 0, 0xAA00FF00);
                break;
            case 3:
                walls.add(new RectangleHitbox("wall1", xPos + 0 * scaleFactor, yPos + 0 * scaleFactor, 138 * scaleFactor, 21 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall2", xPos + 0 * scaleFactor, yPos + 21 * scaleFactor, 18 * scaleFactor, 63 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall3", xPos + 18 * scaleFactor, yPos + 58 * scaleFactor, 30 * scaleFactor, 26 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall4", xPos + 0 * scaleFactor, yPos + 92 * scaleFactor, 99 * scaleFactor, 20 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall5", xPos + 61 * scaleFactor, yPos + 29 * scaleFactor, 35 * scaleFactor, 64 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall6", xPos + 32 * scaleFactor, yPos + 29 * scaleFactor, 29 * scaleFactor, 20 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall7", xPos + 96 * scaleFactor, yPos + 59 * scaleFactor, 16 * scaleFactor, 26 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall8", xPos + 96 * scaleFactor, yPos + 85 * scaleFactor, 3 * scaleFactor, 7 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall9", xPos + 99 * scaleFactor, yPos + 108 * scaleFactor, 109 * scaleFactor, 4 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall10", xPos + 143 * scaleFactor, yPos + 0 * scaleFactor, 65 * scaleFactor, 21 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall11", xPos + 173 * scaleFactor, yPos + 21 * scaleFactor, 35 * scaleFactor, 16 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall12", xPos + 124 * scaleFactor, yPos + 42 * scaleFactor, 60 * scaleFactor, 26 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall13", xPos + 108 * scaleFactor, yPos + 26 * scaleFactor, 60 * scaleFactor, 26 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall14", xPos + 108 * scaleFactor, yPos + 21 * scaleFactor, 30 * scaleFactor, 5 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall15", xPos + 124 * scaleFactor, yPos + 68 * scaleFactor, 37 * scaleFactor, 33 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall16", xPos + 112 * scaleFactor, yPos + 92 * scaleFactor, 12 * scaleFactor, 9 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall17", xPos + 170 * scaleFactor, yPos + 76 * scaleFactor, 38 * scaleFactor, 32 * scaleFactor, 0, 0, 0xAAFF0000));
                walls.add(new RectangleHitbox("wall18", xPos + 193 * scaleFactor, yPos + 37 * scaleFactor, 15 * scaleFactor, 39 * scaleFactor, 0, 0, 0xAAFF0000));
                this.player = new RectangleHitbox("player", xPos + 4 * scaleFactor, yPos + 87 * scaleFactor, 2 * scaleFactor, 2 * scaleFactor, 0, 0, 0xFFFF0000);
                this.finalArea = new RectangleHitbox("finalArea", xPos + 139 * scaleFactor, yPos + 3 * scaleFactor, 3 * scaleFactor, 3 * scaleFactor, 0, 0, 0xAA00FF00);
                break;
        }
    }

    @Override
    public void update() {
        super.update();
        if (status == GameStatus.ACTIVE && isPlayerBoundToMouse) {
            for (RectangleHitbox wall : walls) {
                if (player.intersects(wall)) {
                    status = GameStatus.ENDING;
                    isPlayerBoundToMouse = false;
                    playSound(GameSounds.SCARYMAZE_DEAD, soundVolume, 1.0f);
                    break;
                }
            }
            if (player.intersects(finalArea)) {
                addScore(finalPoints);
                status = GameStatus.ENDING;
                playSound(GameSounds.SCARYMAZE_WIN, soundVolume, 1.0f);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        if (isPlayerBoundToMouse) {
            player.updatePosition(mouseX, mouseY);
        }
        /*for (RectangleHitbox wall : walls) {
            wall.draw(context);
        }*/
        //finalArea.draw(context);
        Identifier playerTexture = Identifier.of(GeoWareMod.MOD_ID, "textures/games/scarymaze/player.png");
        context.drawTexture(playerTexture, (int) player.getXPos() - 1, (int) player.getYPos() - 1,
                (int) player.getWidth() + 2, (int) player.getHeight() + 2, 0, 0, 2, 2, 2, 2);
        player.draw(context);
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        if (button == 0 && status == GameStatus.ACTIVE) {
            if (!isPlayerBoundToMouse && player.isMouseOver(mouseX, mouseY)) {
                isPlayerBoundToMouse = true;
                playSound(GameSounds.SCARYMAZE_SELECT, soundVolume, 1.0f);
            }
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier backgroundTexture;
        Identifier level1 = Identifier.of(GeoWareMod.MOD_ID, "textures/games/scarymaze/1.png");
        Identifier level2 = Identifier.of(GeoWareMod.MOD_ID, "textures/games/scarymaze/2.png");
        Identifier level3 = Identifier.of(GeoWareMod.MOD_ID, "textures/games/scarymaze/3.png");

        switch (level) {
            case 2:
                backgroundTexture = level2;
                break;
            case 3:
                backgroundTexture = level3;
                break;
            default:
                backgroundTexture = level1;
                break;
        }
        context.drawTexture(backgroundTexture, screen.getGameScreenXPos(), screen.getGameScreenYPos(),
                getScreenWidth(), getScreenHeight(), 0, 0, 420, 572, 420, 572);
    }

    @Override
    public int getScreenWidth() {
        return (int) (208 * scaleFactor); // Escala el ancho base
    }

    @Override
    public int getScreenHeight() {
        return (int) (112 * scaleFactor); // Escala la altura base
    }
}