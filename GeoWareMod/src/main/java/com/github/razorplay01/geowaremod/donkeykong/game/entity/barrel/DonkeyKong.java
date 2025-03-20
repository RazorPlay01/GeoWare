package com.github.razorplay01.geowaremod.donkeykong.game.entity.barrel;

import com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity;
import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.GameTask;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import net.minecraft.client.gui.DrawContext;

import java.util.Random;

import static com.github.razorplay01.geowaremod.donkeykong.game.util.TextureProvider.*;

public class DonkeyKong extends DonkeyKongEntity {
    private final Random random = new Random();
    private final float spawnProbability;
    private GameTask spawnTask;

    private final Animation barrelAnimation;
    private final Animation idleAnimation;
    private final Animation extraAnimation;

    private Animation currentAnimation;
    private boolean isSpawningBarrel = false;
    private boolean isPlayingExtra = false;
    private boolean hasSpawnedFirstBarrel = false;

    public DonkeyKong(float xPos, float yPos, GameScreen gameScreen, int spawnInterval, float spawnProbability) {
        super(xPos, yPos, 46, 32, gameScreen, 0xFF0000);
        this.gameScreen = gameScreen;
        this.spawnProbability = spawnProbability;

        float intervalInSeconds = spawnInterval / 1000.0f;

        this.barrelAnimation = new Animation(DONKEY_KONG_BARREL_TEXTURES, intervalInSeconds * 0.3f, false);
        this.extraAnimation = new Animation(DONKEY_KONG_EXTRA_TEXTURES, intervalInSeconds * 0.5f, false);
        this.idleAnimation = new Animation(DONKEY_KONG_IDLE_TEXTURES, 1f, false);

        this.spawnTask = createSpawnTask(spawnInterval);
        this.currentAnimation = idleAnimation;
    }

    private GameTask createSpawnTask(long interval) {
        return new GameTask(() -> {
            if (!game.getPlayer().isWinning() && !isSpawningBarrel && !isPlayingExtra) {
                if (random.nextFloat() < spawnProbability) {
                    // Iniciar animación de barril
                    isSpawningBarrel = true;
                    currentAnimation = barrelAnimation;
                    barrelAnimation.reset();
                } else {
                    // Iniciar animación extra
                    isPlayingExtra = true;
                    currentAnimation = extraAnimation;
                    extraAnimation.reset();
                }
            }
            // Reiniciar la tarea para que se repita cada intervalo
            spawnTask = createSpawnTask(interval);
        }, interval);
    }

    @Override
    public void update() {
        currentAnimation.update();

        if (game.getStatus() == GameStatus.ACTIVE && !hasSpawnedFirstBarrel && !isSpawningBarrel && !isPlayingExtra) {
            isSpawningBarrel = true;
            currentAnimation = barrelAnimation;
            barrelAnimation.reset();
            hasSpawnedFirstBarrel = true;
        }

        if (hasSpawnedFirstBarrel) {
            spawnTask.update();
        }

        if (isSpawningBarrel && barrelAnimation.isFinished()) {
            spawnBarrel(game.getScreen().getGameScreenXPos() + 68f,
                    game.getScreen().getGameScreenYPos() + 70f,
                    gameScreen);
            isSpawningBarrel = false;
            currentAnimation = idleAnimation;
            idleAnimation.reset();
        }

        if (isPlayingExtra && extraAnimation.isFinished()) {
            isPlayingExtra = false;
            currentAnimation = idleAnimation;
            idleAnimation.reset();
        }
    }

    public void spawnBarrel(float x, float y, GameScreen gameScreen) {
        game.getBarrels().add(new Barrel(x, y, gameScreen));
    }

    @Override
    public void render(DrawContext context) {
        renderTexture(context, this, currentAnimation, 0, -5);
    }
}