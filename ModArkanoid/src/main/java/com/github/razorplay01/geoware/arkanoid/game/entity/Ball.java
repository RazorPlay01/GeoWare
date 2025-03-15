package com.github.razorplay01.geoware.arkanoid.game.entity;

import com.github.razorplay01.geoware.arkanoid.game.mapobject.Brick;
import com.github.razorplay01.geoware.arkanoid.game.stages.ArkanoidGame;
import com.github.razorplay01.geoware.arkanoid.game.util.records.Hitbox;
import com.github.razorplay01.geoware.arkanoid.screen.ArkanoidGameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.Iterator;
import java.util.List;

@Getter
@Setter
public class Ball extends Entity {
    private static final float SPEED_INCREMENT = 0.05f;
    private static final float MAX_SPEED = 5.0f;
    private static final float INITIAL_SPEED = 3f;
    private static final float MAX_ANGLE_VARIATION = 0.1f; // Máxima variación aleatoria en radianes (aproximadamente 5.7 grados)
    private static final float MIN_DISTANCE_BETWEEN_COLLISIONS = 8f; // Distancia mínima para permitir otra colisión

    private float currentSpeed;
    private boolean isMoving;
    private float direction;
    private float lastCollisionX;
    private float lastCollisionY;

    private final ArkanoidGame game;

    private boolean isActive;

    public Ball(float xPos, float yPos, float width, float height, ArkanoidGameScreen gameScreen) {
        super(xPos, yPos, width, height, gameScreen, 0xFFFFFFFF);
        this.currentSpeed = INITIAL_SPEED;
        this.isMoving = true;
        this.direction = (float) (-Math.PI / 4);
        this.lastCollisionX = xPos;
        this.lastCollisionY = yPos;
        updateVelocities();
        this.isActive = true;
        this.game = (ArkanoidGame) gameScreen.getGame();
    }

    private void updateVelocities() {
        velocityX = (float) (currentSpeed * Math.cos(direction));
        velocityY = (float) (currentSpeed * Math.sin(direction));
    }

    private float addRandomAngleVariation(float currentDirection) {
        // Genera una variación aleatoria entre -MAX_ANGLE_VARIATION y +MAX_ANGLE_VARIATION
        float variation = (float) ((Math.random() * 2 - 1) * MAX_ANGLE_VARIATION);
        return currentDirection + variation;
    }

    private boolean canProcessCollision(float collisionX, float collisionY) {
        float distance = (float) Math.sqrt(
                Math.pow(collisionX - lastCollisionX, 2) +
                        Math.pow(collisionY - lastCollisionY, 2)
        );
        return distance >= MIN_DISTANCE_BETWEEN_COLLISIONS;
    }

    private void updateLastCollisionPosition(float x, float y) {
        lastCollisionX = x;
        lastCollisionY = y;
    }

    @Override
    public void update() {
        if (!isMoving || game.getPlayer().isLosing()) {
            return;
        }

        // Guardar la posición anterior
        float oldX = xPos;
        float oldY = yPos;

        // Actualizar posición
        xPos += velocityX;
        yPos += velocityY;

        // Verificar colisiones
        checkCollisions();
        checkBrickCollisions(oldX, oldY);
        updateHitboxes();
    }

    private void checkBrickCollisions(float oldX, float oldY) {
        Hitbox ballHitbox = getDefaultHitbox();
        List<Brick> bricks = game.getBricks();
        Iterator<Brick> iterator = bricks.iterator();

        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            Hitbox brickHitbox = brick.getDefaultHitbox();

            if (ballHitbox.intersects(brickHitbox)) {
                float collisionX = xPos + (width / 2);
                float collisionY = yPos + (height / 2);

                // Verificar si ha pasado suficiente distancia desde la última colisión
                if (!canProcessCollision(collisionX, collisionY)) {
                    continue;
                }

                // Determinar la dirección de la colisión usando la posición anterior
                boolean hitVertical = false;
                boolean hitHorizontal = false;

                // Comprobar colisión vertical
                if (oldY + height <= brickHitbox.yPos() || oldY >= brickHitbox.yPos() + brickHitbox.height()) {
                    hitVertical = true;
                }

                // Comprobar colisión horizontal
                if (oldX + width <= brickHitbox.xPos() || oldX >= brickHitbox.xPos() + brickHitbox.width()) {
                    hitHorizontal = true;
                }

                // Aplicar rebote según la dirección de la colisión
                if (hitVertical) {
                    direction = addRandomAngleVariation(-direction);
                    yPos = oldY;
                }
                if (hitHorizontal) {
                    direction = addRandomAngleVariation((float) (Math.PI - direction));
                    xPos = oldX;
                }
                if (!hitVertical && !hitHorizontal) {
                    direction = addRandomAngleVariation((float) (Math.PI + direction));
                }

                updateVelocities();
                updateLastCollisionPosition(collisionX, collisionY);

                // Incrementar velocidad y actualizar puntaje
                currentSpeed = Math.min(currentSpeed + SPEED_INCREMENT, MAX_SPEED);
                game.getPlayer().addScore(brick.getXPos() + brick.getWidth() / 2, brick.getYPos(), 100);
                iterator.remove();
                game.removeBrick(brick);
                break;
            }
        }
    }

    private void checkCollisions() {
        // Colisión con los bordes de la pantalla
        int screenX = gameScreen.getGameScreenXPos();
        int screenY = gameScreen.getGameScreenYPos();
        int screenWidth = gameScreen.getGame().getScreenWidth();
        int screenHeight = gameScreen.getGame().getScreenHeight();

        // Colisión con borde izquierdo
        if (xPos <= screenX) {
            xPos = screenX;
            direction = (float) (Math.PI - direction);
            updateVelocities();
        }
        // Colisión con borde derecho
        else if (xPos + width >= screenX + screenWidth) {
            xPos = screenX + screenWidth - width;
            direction = (float) (Math.PI - direction);
            updateVelocities();
        }
        // Colisión con borde superior
        if (yPos <= screenY) {
            yPos = screenY;
            direction = -direction;
            updateVelocities();
        }
        // Colisión con borde inferior
        else if (yPos + height >= screenY + screenHeight) {
            // En lugar de hacer perder al jugador, eliminamos esta bola
            this.isActive = false;
            return;
        }

        // Colisión con el jugador
        Player player = game.getPlayer();
        if (!player.isLosing()) {
            checkPlayerCollision(player);
        }
    }

    private void checkPlayerCollision(Player player) {
        Hitbox playerDefaultHitbox = player.getDefaultHitbox();
        Hitbox ballHitbox = getDefaultHitbox();

        if (ballHitbox.intersects(playerDefaultHitbox)) {
            yPos = playerDefaultHitbox.yPos() - height;

            // Calcular el punto de impacto relativo en el jugador (-1 a 1)
            float hitPoint = (xPos + (width / 2) - (playerDefaultHitbox.xPos() + playerDefaultHitbox.width() / 2))
                    / (playerDefaultHitbox.width() / 2);

            // Limitar el hitPoint entre -1 y 1
            hitPoint = Math.clamp(hitPoint, -1, 1);

            // Ángulo base (90 grados en radianes)
            float baseAngle = -(float) Math.PI / 2;

            // Máxima desviación del ángulo (en radianes)
            float maxAngleDeviation = (float) Math.PI / 3; // 60 grados

            // Calcular el nuevo ángulo
            direction = baseAngle + (hitPoint * maxAngleDeviation);

            // Actualizar velocidades
            updateVelocities();

            // Verificar hitboxes especiales del jugador para boost
            Hitbox leftBoostHitbox = player.getHitboxByName("boost_ball_hitbox_l");
            Hitbox rightBoostHitbox = player.getHitboxByName("boost_ball_hitbox_r");

            if (ballHitbox.intersects(leftBoostHitbox) || ballHitbox.intersects(rightBoostHitbox)) {
                currentSpeed = Math.min(currentSpeed * 1.2f, MAX_SPEED);
                updateVelocities();
            }

            updateLastCollisionPosition(xPos + (width / 2), yPos + (height / 2));
        }
    }

    public void start() {
        if (!isMoving && !game.getPlayer().isLosing()) {
            isMoving = true;
        }
    }

    @Override
    public void render(DrawContext context) {
        renderHitboxes(context);
    }
}
