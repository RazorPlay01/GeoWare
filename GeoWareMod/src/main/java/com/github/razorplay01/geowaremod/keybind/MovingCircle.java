package com.github.razorplay01.geowaremod.keybind;

import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

@Getter
@Setter
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

    public MovingCircle(float radius, float speed, int key, Game game) {
        this.game = (KeyBindGame) game;

        xPos = game.getScreen().getGameScreenXPos() + game.getScreenWidth() - radius * 2 - 5;
        yPos = game.getScreen().getGameScreenYPos() + game.getScreenHeight() / 2f;

        this.keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName == null) {
            keyName = Character.toString((char) key);
        }

        this.radius = radius;
        this.speed = speed;
        this.key = key;
        this.active = true;
        this.hitbox = new CircleHitbox("moving", xPos - radius, yPos - radius, radius, 0, 0, 0xFFFFFF00);
    }

    public void update() {
        if (!active) return;
        xPos -= speed;

        // Detectar colisión con bordes *antes* de que la bola se salga
        if (xPos + radius > game.getScreen().getGameScreenXPos() + game.getScreenWidth() || // Borde derecho
                xPos - radius < game.getScreen().getGameScreenXPos() || // Borde izquierdo
                yPos + radius > game.getScreen().getGameScreenYPos() + game.getScreenHeight() || // Borde inferior
                yPos - radius < game.getScreen().getGameScreenYPos()) { // Borde superior
            active = false;
            return; // Importante: salir del método para evitar actualizar el hitbox
        }

        hitbox.updatePosition(this.xPos, this.yPos);
    }

    public void draw(DrawContext context) {
        hitbox.draw(context);
        int textWidth = game.getTextRenderer().getWidth(keyName.toUpperCase());
        int textX = (int) (xPos - textWidth / 2f);
        int textY = (int) (yPos - 5);

        context.drawText(game.getTextRenderer(), keyName.toUpperCase(), textX, textY, 0xFFFFFFFF, true);
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
            }
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

            if (precision > 0.90f) {
                return 50;
            } else if (precision > 0.75f) {
                return 30;
            } else {
                return 10;
            }
        }
        return 0;
    }
}