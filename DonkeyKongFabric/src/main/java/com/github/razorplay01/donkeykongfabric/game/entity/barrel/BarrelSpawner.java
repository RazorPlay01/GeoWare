package com.github.razorplay01.donkeykongfabric.game.entity.barrel;

import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BarrelSpawner {
    private final Random random = new Random();
    private final GameScreen gameScreen;
    private int spawnTimer;
    private final int spawnInterval;
    private final float spawnProbability;

    public BarrelSpawner(GameScreen gameScreen, int spawnInterval, float spawnProbability) {
        this.gameScreen = gameScreen;
        this.spawnInterval = spawnInterval;
        this.spawnProbability = spawnProbability;
        this.spawnTimer = 0;
    }

    public void update() {
        spawnTimer++;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            if (random.nextFloat() < spawnProbability && !gameScreen.getTestGame().getPlayer().isWinning()) {
                spawnBarrel(gameScreen.getTestGame().getScreen().getScreenXPos() + 45f, gameScreen.getTestGame().getScreen().getScreenYPos() + 71f, gameScreen);
            }
        }
    }

    public void removeAndSpawnBarrels(DrawContext context) {
        List<Barrel> barrelsToRemove = new ArrayList<>();
        for (Barrel barrel : gameScreen.getTestGame().getBarrels()) {
            if (barrel.isRemove()) {
                barrelsToRemove.add(barrel);
            }
            barrel.update();
            barrel.render(context);
        }
        gameScreen.getTestGame().getBarrels().removeAll(barrelsToRemove);
    }

    public void spawnBarrel(float x, float y, GameScreen gameScreen) {
        gameScreen.getTestGame().getBarrels().add(new Barrel(x, y, gameScreen));
    }
}