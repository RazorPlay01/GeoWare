package com.github.razorplay01.modbubblepuzzle;

import com.github.razorplay01.modbubblepuzzle.util.game.GameScreen;
import com.github.razorplay01.modbubblepuzzle.util.game.GameStatus;
import com.github.razorplay01.modbubblepuzzle.util.hitbox.RectangleHitbox;
import com.github.razorplay01.modbubblepuzzle.util.render.CustomDrawContext;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.util.List;
import java.util.Random;

@Getter
public class Player {
    private float x, y; // Posición fija del jugador
    private float width, height; // Tamaño del jugador
    private float angle; // Ángulo de disparo
    private Bubble currentBubble; // Burbuja actual
    private Bubble nextBubble; // Burbuja siguiente
    private RectangleHitbox hitbox; // Hitbox del jugador
    private GameScreen screen;
    private Random random = new Random();

    public Player(float x, float y, float width, float height, GameScreen screen) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = 90; // Ángulo inicial (disparo hacia arriba)
        this.screen = screen;
        this.hitbox = new RectangleHitbox("player", x, y, width, height, 0, 0, 0xFF00FF00);
        this.currentBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
        this.nextBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
    }

    public void rotateLeft() {
        this.angle += 2; // Rotar a la izquierda
        if (this.angle >= 180) this.angle -= 180; // Mantener el ángulo entre 0 y 180
    }

    public void rotateRight() {
        this.angle -= 2; // Rotar a la derecha
        if (this.angle < 0) this.angle += 180; // Mantener el ángulo entre 0 y 180
    }

    public Bubble shoot() {
        List<Bubble> bubbles = ((BubblePuzzleGame) screen.getGame()).getBubbles();
        boolean isAnyBubbleMoving = bubbles.stream().anyMatch(bubble -> bubble.isMoving() || bubble.isFalling());

        if (screen.getGame().getStatus() == GameStatus.ACTIVE && !isAnyBubbleMoving) {
            currentBubble.launch(angle, 10); // Disparar la bola actual
            Bubble shotBubble = currentBubble;

            // Centrar la siguiente bola
            currentBubble = nextBubble;
            currentBubble.setX(x + width / 2f);
            currentBubble.setY(y + height / 2f);

            // Generar una nueva bola siguiente y centrarla
            nextBubble = new Bubble(x + width / 2f, y + height / 2f, 10, generateRandomColor(), screen);
            return shotBubble;
        }
        return null;
    }

    private int generateRandomColor() {
        int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00}; // Rojo, verde, azul, amarillo
        return colors[random.nextInt(colors.length)];
    }

    /*private int generateRandomColor() {
        Set<Integer> availableColors = new HashSet<>();
        for (Bubble bubble : ((BubblePuzzleGame) screen.getGame()).getBubbles()) {
            availableColors.add(bubble.getColor());
        }

        if (availableColors.isEmpty()) {
            int[] defaultColors = {BubbleColor.RED.getColor(), BubbleColor.BLUE.getColor(), BubbleColor.GREEN.getColor(), BubbleColor.YELLOW.getColor()};
            return defaultColors[random.nextInt(defaultColors.length)];
        }

        List<Integer> colorList = new ArrayList<>(availableColors);
        int randomIndex = random.nextInt(colorList.size());
        return colorList.get(randomIndex);
    }*/

    public void draw(DrawContext context) {
        CustomDrawContext customDrawContext = CustomDrawContext.wrap(context);
        // hitbox.draw(context); // Dibujar al jugador
        nextBubble.draw(context); // Dibujar la burbuja siguiente

        // Dibujar la línea de dirección de disparo
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;
        float lineLength = 50; // Longitud de la línea
        float endX = centerX + (float) Math.cos(Math.toRadians(angle)) * lineLength;
        float endY = centerY - (float) Math.sin(Math.toRadians(angle)) * lineLength;
        customDrawContext.drawLine((int) centerX, (int) centerY, (int) endX, (int) endY, 0xFFFFFFFF); // Línea blanca
        currentBubble.draw(context); // Dibujar la burbuja actual
    }
}
