package com.github.razorplay01.geowaremod.arkanoid.entity;

import com.github.razorplay01.geowaremod.arkanoid.CollisionHandler;
import com.github.razorplay01.razorplayapi.util.hitbox.CircleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

@Getter
@Setter
public class Ball extends ArkanoidEntity {
    public static final float SPEED_INCREMENT = 0.05f;
    public static final float MAX_SPEED = 5.0f;
    public static final float INITIAL_SPEED = 3f;
    public static final float MAX_ANGLE_VARIATION = 0.1f;
    public static final float MIN_DISTANCE_BETWEEN_COLLISIONS = 8f;

    private float currentSpeed;
    private boolean isMoving;
    private float direction;
    private float lastCollisionX;
    private float lastCollisionY;
    private boolean isActive;

    private final CollisionHandler collisionHandler;

    public Ball(float xPos, float yPos, float radius, GameScreen gameScreen) {
        super(xPos, yPos, radius * 2, radius * 2, gameScreen, 0xFFFFFFFF); // width y height son diámetro
        this.currentSpeed = INITIAL_SPEED;
        this.isMoving = true;
        this.direction = (float) (-Math.PI / 4);
        this.lastCollisionX = xPos;
        this.lastCollisionY = yPos;
        this.isActive = true;
        this.hitboxes.clear(); // Limpiar hitbox rectangular por defecto
        this.hitboxes.add(new CircleHitbox("default", xPos, yPos, radius, 0, 0, 0xFFFFFFFF));
        this.collisionHandler = new CollisionHandler(arkanoidGame);
        updateVelocities();
    }

    public void updateVelocities() {
        velocityX = (float) (currentSpeed * Math.cos(direction));
        velocityY = (float) (currentSpeed * Math.sin(direction));
    }

    public boolean canProcessCollision(float collisionX, float collisionY) {
        float distance = (float) Math.sqrt(
                Math.pow(collisionX - lastCollisionX, 2) +
                        Math.pow(collisionY - lastCollisionY, 2)
        );
        return distance >= MIN_DISTANCE_BETWEEN_COLLISIONS;
    }

    public float addRandomAngleVariation(float currentDirection) {
        float variation = (float) ((Math.random() * 2 - 1) * MAX_ANGLE_VARIATION);
        return currentDirection + variation;
    }

    public void updateLastCollisionPosition(float x, float y) {
        lastCollisionX = x;
        lastCollisionY = y;
    }

    @Override
    public void update() {
        if (!isMoving || arkanoidGame.getPlayer().isLosing()) {
            return;
        }

        float oldX = xPos;
        float oldY = yPos;

        xPos += velocityX;
        yPos += velocityY;

        collisionHandler.checkBallCollisions(this, oldX, oldY);
        updateHitboxes();
    }

    public void start() {
        if (!isMoving && !arkanoidGame.getPlayer().isLosing()) {
            isMoving = true;
        }
    }
}