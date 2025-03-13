package com.github.razorplay01.modbubblepuzzle;

import com.github.razorplay01.modbubblepuzzle.util.hitbox.CircleHitbox;
import com.github.razorplay01.modbubblepuzzle.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class Bubble {
    private float x;
    private float y;
    private float radius; // Radio de la burbuja
    private int color; // Color de la burbuja
    private CircleHitbox hitbox; // Hitbox circular
    private float velocityX, velocityY; // Velocidad de la burbuja
    private boolean isMoving; // Indica si la burbuja está en movimiento
    private boolean isFalling; // Indica si la burbuja está en movimiento
    private boolean connectedToTop; // Indica si está conectada al borde superior
    private List<Bubble> connections; // Lista de burbujas conectadas
    private BubblePuzzleScreen screen;

    public Bubble(float x, float y, float radius, int color, GameScreen screen) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.hitbox = new CircleHitbox("bubble", x, y, radius, 0, 0, color);
        this.velocityX = 0;
        this.velocityY = 0;
        this.isMoving = false;
        this.isFalling = false;
        this.connectedToTop = false; // Inicialmente no está conectada al borde superior
        this.connections = new ArrayList<>(); // Inicialmente no tiene conexiones
        this.screen = (BubblePuzzleScreen) screen;
    }

    public void update() {
        if (isFalling) {
            this.isMoving = true;
            y += velocityY;
            updatePosition(x, y);

            BubblePuzzleGame game = (BubblePuzzleGame) screen.getGame();
            float gameBottomBorder = screen.getGameScreenYPos() + game.getScreenHeight();
            if (y + radius >= gameBottomBorder) {
                game.removeBubble(this);
            }
        }

        if (isMoving && !isFalling) {
            float originalX = x;
            float originalY = y;
            float remainingX = velocityX;
            float remainingY = velocityY;
            int steps = 30; // Aumentar la granularidad
            float stepX = velocityX / steps;
            float stepY = velocityY / steps;

            for (int i = 0; i < steps; i++) {
                float nextX = x + stepX;
                float nextY = y + stepY;

                // Verificar colisiones antes de mover
                updatePosition(nextX, nextY);
                boolean collisionDetected = false;

                for (Bubble otherBubble : ((BubblePuzzleGame) screen.getGame()).getBubbles()) {
                    if (this != otherBubble && this.getHitbox().intersects(otherBubble.getHitbox())) {
                        collisionDetected = true;
                        break;
                    }
                }

                if (collisionDetected) {
                    // Revertir al último estado válido y detener
                    updatePosition(originalX + (i * stepX), originalY + (i * stepY));
                    stop();
                    // Notificar al juego para manejar la colisión
                    ((BubblePuzzleGame) screen.getGame()).checkCollisions(this);
                    return;
                }

                x = nextX;
                y = nextY;
                originalX = x;
                originalY = y;
            }

            updatePosition(x, y);
        }
    }

    public void addConnection(Bubble other) {
        if (!connections.contains(other)) {
            connections.add(other);
        }
    }

    public void propagateTopConnection() {
        if (connectedToTop) return; // Si ya está conectada al tope, no hacer nada

        connectedToTop = true; // Marcar como conectada al tope

        // Propagar el estado a todas las burbujas conectadas
        for (Bubble neighbor : connections) {
            if (!neighbor.isConnectedToTop()) {
                neighbor.propagateTopConnection();
            }
        }
    }

    public void startFalling() {
        if (!isFalling) {
            this.isFalling = true;
            this.velocityX = 0; // Sin movimiento horizontal al caer
            this.velocityY = 5; // Velocidad constante de caída
            this.isMoving = true; // Marcar como en movimiento
        }
    }

    public void updatePosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        this.hitbox.updatePosition(newX, newY);
    }

    public void launch(float angle, float speed) {
        this.velocityX = (float) Math.cos(Math.toRadians(angle)) * speed;
        this.velocityY = -(float) Math.sin(Math.toRadians(angle)) * speed; // Negativo porque el eje Y está invertido
        this.isMoving = true;
    }

    public void stop() {
        this.velocityX = 0;
        this.velocityY = 0;
        this.isMoving = false;
    }

    public void draw(DrawContext context) {
        hitbox.draw(context); // Dibujar la burbuja
    }
}