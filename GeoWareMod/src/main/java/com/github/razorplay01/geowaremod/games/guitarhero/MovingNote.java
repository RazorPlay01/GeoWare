package com.github.razorplay01.geowaremod.games.guitarhero;

import com.github.razorplay01.razorplayapi.util.Particle;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class MovingNote {
    private float xPos;
    private float yPos;
    private final float size;
    private final float speed;
    @Getter
    private final int key;
    @Getter
    private boolean active;
    private final GuitarHeroGame game;
    @Getter
    private final long spawnTime;
    private final long hitTime;

    public MovingNote(float size, long spawnTime, long hitTime, int keyCode, GuitarHeroGame game) {
        this.size = size;
        this.key = keyCode;
        this.game = game;
        this.spawnTime = spawnTime;
        this.hitTime = hitTime;

        float distance = GuitarHeroGame.TARGET_Y - (-size);
        float timeInSeconds = (hitTime - spawnTime) / 1000.0f;
        this.speed = distance / timeInSeconds;

        this.xPos = game.getScreen().getGameScreenXPos() + switch (keyCode) {
            case GLFW.GLFW_KEY_F -> 25;
            case GLFW.GLFW_KEY_G -> 75;
            case GLFW.GLFW_KEY_H -> 125;
            case GLFW.GLFW_KEY_J -> 175;
            default -> 25;
        };
        this.yPos = game.getScreen().getGameScreenYPos() - size;
        this.active = true;
    }

    public void update(float delta) {
        if (!active || delta <= 0) return;
        yPos += speed * delta;

        if (yPos > game.getScreen().getGameScreenYPos() + game.getScreenHeight()) {
            active = false;
        }
    }

    public void draw(DrawContext context) {
        if (!active) return;
        int color = switch (key) {
            case GLFW.GLFW_KEY_F -> 0xFFFF0000; // Rojo
            case GLFW.GLFW_KEY_G -> 0xFF00FF00; // Verde
            case GLFW.GLFW_KEY_H -> 0xFF0000FF; // Azul
            case GLFW.GLFW_KEY_J -> 0xFFFFFF00; // Amarillo
            default -> 0xFFFFFFFF; // Blanco (no debería pasar)
        };
        context.fill((int) (xPos - size / 2), (int) (yPos - size / 2),
                (int) (xPos + size / 2), (int) (yPos + size / 2), color);
    }

    public boolean checkKey(int keyCode) {
        if (!active || keyCode != key) return false;

        long currentTime = System.currentTimeMillis() - game.getGameStartTime();
        long timeError = Math.abs(currentTime - hitTime);
        float distanceToTarget = Math.abs(yPos - (game.getScreen().getGameScreenYPos() + GuitarHeroGame.TARGET_Y));

        if (timeError < 200 && distanceToTarget < size * 2) {
            int points = calculatePoints(timeError);
            if (points > 0) {
                game.addScore(points, xPos, yPos);
                game.addParticle(new Particle(xPos - size / 2, yPos - size / 2, size, size, game.getScreen(), null));
            }
            active = false;
            return true;
        }
        return false;
    }

    private int calculatePoints(long timeError) {
        if (timeError < 50) return 5;
        else if (timeError < 100) return 3;
        else if (timeError < 200) return 1;
        return 0;
    }
}