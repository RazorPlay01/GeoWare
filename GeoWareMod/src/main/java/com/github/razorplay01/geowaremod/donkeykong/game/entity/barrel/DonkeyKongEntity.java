package com.github.razorplay01.geowaremod.donkeykong.game.entity.barrel;

import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.razorplay01.geowaremod.donkeykong.game.util.TextureProvider.*;

public class DonkeyKongEntity extends com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity {
    private final Random random = new Random();
    private int spawnTimer;
    private final int spawnInterval;
    private final float spawnProbability;

    private final Animation barrelAnimation = new Animation(DONKEY_KONG_BARREL_TEXTURES, 0.7f, false);
    private final Animation idleAnimation = new Animation(DONKEY_KONG_IDLE_TEXTURES, 1f, false);
    private final Animation extraAnimation = new Animation(DONKEY_KONG_EXTRA_TEXTURES, 1f, false);

    private Animation currentAnimation;

    private boolean isSpawningBarrel = false;
    private boolean isPlayingExtra = false;

    public DonkeyKongEntity(float xPos, float yPos, GameScreen gameScreen, int spawnInterval, float spawnProbability) {
        super(xPos, yPos, 46, 32, gameScreen, 0xFF0000); // Ajusta el tamaño según tus necesidades
        this.gameScreen = gameScreen;

        this.spawnInterval = spawnInterval;
        this.spawnProbability = spawnProbability;
        this.spawnTimer = 0;

        // Comenzar con la animación idle
        this.currentAnimation = idleAnimation;
    }

    @Override
    public void update() {
        spawnTimer++;

        // Actualizar la animación actual
        currentAnimation.update();

        // Si no está en medio de una animación, verificar si debe spawnear un barril
        if (!isSpawningBarrel && !isPlayingExtra && spawnTimer >= spawnInterval) {
            spawnTimer = 0;

            if (random.nextFloat() < spawnProbability && !game.getPlayer().isWinning()) {
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

        // Verificar si la animación de barril ha terminado
        if (isSpawningBarrel && barrelAnimation.isFinished()) {
            spawnBarrel(game.getScreen().getGameScreenXPos() + 68f, game.getScreen().getGameScreenYPos() + 70f, gameScreen);

            isSpawningBarrel = false;
            currentAnimation = idleAnimation;
            idleAnimation.reset();
        }

        // Verificar si la animación extra ha terminado
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

    public void removeAndSpawnBarrels(DrawContext context) {
        List<Barrel> barrelsToRemove = new ArrayList<>();
        for (Barrel barrel : game.getBarrels()) {
            if (barrel.isRemove()) {
                barrelsToRemove.add(barrel);
            }
            barrel.update();
            barrel.render(context);
        }
        game.getBarrels().removeAll(barrelsToRemove);
    }
}