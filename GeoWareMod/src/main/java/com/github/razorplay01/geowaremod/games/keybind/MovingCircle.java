package com.github.razorplay01.geowaremod.games.keybind;

import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import com.github.razorplay01.razorplayapi.util.texture.Texture;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@Getter
public class MovingCircle {
    private float xPos;
    private float yPos;
    private final float radius;
    private float speed;
    private final int key;
    private String keyName;
    private boolean active;
    private final CircleHitbox hitbox;
    private final KeyBindGame game;
    private final Texture keyTexture;
    private final Animation keyAnimation;

    public MovingCircle(float radius, float speed, int keyCode, Texture keyTexture, Animation keyAnimation, KeyBindGame game) {
        this.game = game;
        this.radius = radius;
        this.speed = speed;
        this.key = keyCode;
        this.keyTexture = keyTexture;
        this.keyAnimation = keyAnimation;

        xPos = game.getScreen().getGameScreenXPos() + game.getScreenWidth() - radius * 2 - 5;
        yPos = game.getScreen().getGameScreenYPos() + 29;

        this.keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName == null) {
            keyName = Character.toString((char) key);
        }

        this.active = true;
        this.hitbox = new CircleHitbox("moving", xPos - radius, yPos - radius, radius, 0, 0, 0xFFFFFF00);
    }

    public void update() {
        if (!active) return;
        xPos -= speed;

        if (xPos + radius > game.getScreen().getGameScreenXPos() + game.getScreenWidth() ||
                xPos - radius < game.getScreen().getGameScreenXPos() ||
                yPos + radius > game.getScreen().getGameScreenYPos() + game.getScreenHeight() ||
                yPos - radius < game.getScreen().getGameScreenYPos()) {
            active = false;
            return;
        }

        hitbox.updatePosition(this.xPos, this.yPos);
    }

    public void draw(DrawContext context) {
        if (!active) return;
        // Renderizar la textura estática del botón
        context.drawTexture(
                keyTexture.identifier(),
                (int) (xPos - radius), (int) (yPos - radius),
                (int) (radius * 2), (int) (radius * 2),
                keyTexture.u(), keyTexture.v(),
                keyTexture.width(), keyTexture.height(),
                keyTexture.textureWidth(), keyTexture.textureHeight()
        );
    }

    public boolean collidesWith(CircleHitbox other) {
        return hitbox.intersects(other);
    }

    public boolean checkKey(int keyCode) {
        if (!active) return false;

        if (keyCode == key) {
            if (!collidesWith(game.getFinalCircle())) {
                active = false;
                return true;
            }

            int points = calculatePoints();
            if (points > 0) {
                game.addScore(points, game.getFinalCircle().getXPos(), game.getFinalCircle().getYPos());
                game.addParticle(new Particle(
                        game.getFinalCircle().getXPos() - radius,
                        game.getFinalCircle().getYPos() - radius,
                        radius * 2, radius * 2,
                        game.getScreen(),
                        keyAnimation
                ));
                game.playRandomEmote(); // Reproducir un emote aleatorio
            }
            active = false;
            return true;
        }
        active = false;
        return true;
    }

    private int calculatePoints() {
        float dx = game.getFinalCircle().getXPos() - xPos;
        float dy = game.getFinalCircle().getYPos() - yPos;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float overlap = 2 * radius - distance;

        if (overlap > 0) {
            float precision = overlap / (2 * radius);
            if (precision > 0.90f) return 5;
            else if (precision > 0.75f) return 3;
            else return 1;
        }
        return 0;
    }
}