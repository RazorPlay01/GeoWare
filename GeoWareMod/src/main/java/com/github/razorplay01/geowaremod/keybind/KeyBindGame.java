package com.github.razorplay01.geowaremod.keybind;

import com.github.razorplay01.razorplayapi.util.GameStatus;
import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.render.CustomDrawContext;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.stage.Game;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Getter
public class KeyBindGame extends Game {
    private static final int GAME_WIDTH = 20 * 18; // Ancho de la zona de juego
    private static final int GAME_HEIGHT = 20 * 8; // Alto de la zona de juego
    private static final int CIRCLE_RADIUS = 10;
    private static final float CIRCLE_SPEED = 2.0f;
    private CircleHitbox finalCircle;
    private final List<MovingCircle> movingCircles;
    private final Random random;

    public KeyBindGame(GameScreen screen, int timeLimitSeconds, int prevScore) {
        super(screen, 5, timeLimitSeconds, prevScore);
        movingCircles = new ArrayList<>();
        random = new Random();
    }

    @Override
    public void init() {
        super.init();
        finalCircle = new CircleHitbox("test", screen.getGameScreenXPos() + (CIRCLE_RADIUS * 2f) * 2, screen.getGameScreenYPos() + GAME_HEIGHT / 2f - CIRCLE_RADIUS / 2f,
                10, 5, 5, 0xFF00FF00);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        finalCircle.draw(context);
        for (MovingCircle circle : movingCircles) {
            circle.draw(context);
        }
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
        Iterator<MovingCircle> iterator = movingCircles.iterator();
        while (iterator.hasNext()) {
            MovingCircle circle = iterator.next();
            if (circle.checkKey(keyCode)) {
                iterator.remove(); // Eliminar el círculo directamente usando el iterador
                break; // Solo procesar el primer círculo coincidente
            }
        }
    }

    @Override
    public void handleMouseInput(double mouseX, double mouseY, int button) {
        // []
    }

    @Override
    public void update() {
        super.update();

        if (status == GameStatus.ACTIVE) {
            // Generar nuevos círculos (puedes ajustar la frecuencia)
            if (random.nextInt(100) < 5) {
                int randomKey = getRandomKey();
                if (!isCircleAtSpawnPosition()) { // Verifica si hay una bola en la posición
                    movingCircles.add(new MovingCircle(CIRCLE_RADIUS, CIRCLE_SPEED, randomKey, this));
                }
            }

            ArrayList<MovingCircle> circlesToRemove = new ArrayList<>(); // Lista para eliminar círculos

            for (MovingCircle circle : movingCircles) {
                circle.update();

                if (!circle.isActive()) {
                    circlesToRemove.add(circle); // Añadir a la lista para eliminar
                }
            }

            movingCircles.removeAll(circlesToRemove);
        }
    }

    private boolean isCircleAtSpawnPosition() {
        for (MovingCircle circle : movingCircles) {
            // Verifica si la posición de spawn coincide con la de otro círculo activo
            if (Math.abs(circle.getXPos() - (getScreen().getGameScreenXPos() + getScreenWidth() - CIRCLE_RADIUS * 2 - 5)) < CIRCLE_RADIUS * 2) {
                return true; // Ya hay un círculo en esta posición
            }
        }
        return false; // No hay ningún círculo en esta posición
    }

    private int getRandomKey() {
        int[] validKeys = {GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_D,
                GLFW.GLFW_KEY_I, GLFW.GLFW_KEY_J, GLFW.GLFW_KEY_K, GLFW.GLFW_KEY_L};
        return validKeys[random.nextInt(validKeys.length)];
    }
}