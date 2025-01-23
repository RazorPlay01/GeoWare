package com.github.razorplay01.donkeykongfabric.game.entity;

import com.github.razorplay01.donkeykongfabric.game.entity.player.Player;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.util.Animation;
import com.github.razorplay01.donkeykongfabric.game.util.records.Hitbox;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.github.razorplay01.donkeykongfabric.game.util.records.Hitbox.*;
import static com.github.razorplay01.donkeykongfabric.game.util.texture.TextureProvider.FIRE_L_TEXTURES;
import static com.github.razorplay01.donkeykongfabric.game.util.texture.TextureProvider.FIRE_R_TEXTURES;

@Getter
public class Fire extends Entity {
    private final Animation horizontalRightAnimation = new Animation(FIRE_R_TEXTURES, 0.5f, true);
    private final Animation horizontalLeftAnimation = new Animation(FIRE_L_TEXTURES, 0.5f, true);


    private static final float MOVEMENT_SPEED = 0.5f;
    private static final float LADDER_SPEED = 0.25f;
    private static final float PLAYER_DETECTION_RANGE = 16f;
    private static final float DECISION_COOLDOWN = 1.0f;
    private static final float RANDOM_MOVEMENT_CHANCE = 0.3f;

    private final Random random = new Random();
    private float decisionTimer = 0;
    private final Player targetPlayer;

    public Fire(float x, float y, GameScreen gameScreen) {
        super(x, y, 16, 16, gameScreen, 0xFFfff700); // Cambiado a 16x16
        this.targetPlayer = gameScreen.getTestGame().getPlayer();
        this.velocityX = MOVEMENT_SPEED;
// Hitbox más alta para las escaleras para poder engancharse mejor
        this.hitboxes.add(new Hitbox(HITBOX_LADDER, xPos, yPos, 16, 16, 0, 0, LADDER_HITBOX_COLOR));
        this.hitboxes.add(new Hitbox(HITBOX_PLAYER, xPos + 4, yPos + 4, 8, 8, 4, 4, PLAYER_HITBOX_COLOR));
    }


    @Override
    public void update() {
        decisionTimer -= 1f / 60f;


        if (decisionTimer <= 0) {
            makeMovementDecision();
            decisionTimer = DECISION_COOLDOWN;
        }

        if (isOnLadder) {
            handleLadderMovement();
        } else {
            handleNormalMovement();
        }

        updateHitboxes();
        verifyScreenBoundsCollision();
    }

    private void makeMovementDecision() {
        if (random.nextFloat() < RANDOM_MOVEMENT_CHANCE) {
            velocityX = (random.nextBoolean() ? 1 : -1) * MOVEMENT_SPEED;
            return;
        }


        float playerDistance = targetPlayer.getXPos() - xPos;
        float playerHeightDiff = targetPlayer.getYPos() - yPos;

        if (Math.abs(playerHeightDiff) < PLAYER_DETECTION_RANGE) {
            velocityX = Math.signum(playerDistance) * MOVEMENT_SPEED;
        } else {
            findNearestLadder(playerHeightDiff > 0);
        }
    }

    private void findNearestLadder(boolean goingUp) {
        List<Ladder> ladders = gameScreen.getTestGame().getLadders();
        Ladder nearestLadder = null;
        float minDistance = Float.MAX_VALUE;

        for (Ladder ladder : ladders) {
            if (Math.abs(ladder.getXPos() - xPos) < minDistance && ((goingUp && ladder.getYPos() < yPos) || (!goingUp && ladder.getYPos() > yPos))) {
                nearestLadder = ladder;
                minDistance = Math.abs(ladder.getXPos() - xPos);
            }
        }

        if (nearestLadder != null) {
            velocityX = Math.signum(nearestLadder.getXPos() - xPos) * MOVEMENT_SPEED;
        }
    }

    private void handleLadderMovement() {
        if (currentLadder == null) return;
        float targetY = targetPlayer.getYPos() > yPos ? LADDER_SPEED : -LADDER_SPEED;
// Verificar si hay una plataforma arriba antes de moverse
        if (targetY < 0) {
            Platform platformAbove = findPlatformAbove();
            if (platformAbove != null && yPos <= platformAbove.getYPos()) {
                yPos = platformAbove.getYPos() - height; // Asegúrate de que el fuego esté sobre la plataforma
                exitLadder();
                return;
            }
        }


        yPos += targetY;
        xPos = currentLadder.getXPos() + (currentLadder.getWidth() - width) / 2;
// Actualizar animación
        if (velocityX > 0) {
            horizontalRightAnimation.update();
        } else {
            horizontalLeftAnimation.update();
        }

// Verificar si debemos salir de la escalera
        if (!isValidLadderPosition()) {
            exitLadder();
        }
    }


    private boolean isValidLadderPosition() {
        if (currentLadder == null) return false;

        float ladderTop = currentLadder.getYPos() - height; // Ajustar para considerar la altura del fuego
        float ladderBottom = currentLadder.getYPos() + currentLadder.getHeight();

        return yPos >= ladderTop && yPos <= ladderBottom;
    }

    private void handleNormalMovement() {
        velocityY += gravity;
        velocityY = Math.min(velocityY, maxFallSpeed);


        Platform stepPlatform = findStepPlatform(gameScreen.getTestGame().getPlatforms());
        if (stepPlatform != null) {
            yPos = stepPlatform.getYPos() - height;
            velocityY = 0;
            currentPlatform = stepPlatform;  // Actualizar la plataforma actual
        } else {
            yPos += velocityY;
            xPos += velocityX;

            checkPlatformCollisions();
        }

        checkLadderCollisions();
// Actualizar animación
        if (velocityX > 0) {
            horizontalRightAnimation.update();
        } else {
            horizontalLeftAnimation.update();
        }
    }


    private void checkPlatformCollisions() {
        for (Platform platform : gameScreen.getTestGame().getPlatforms()) {
            if (isCollidingWithPlatform(platform)) {
                yPos = platform.getYPos() - height;
                velocityY = 0;
                currentPlatform = platform; // Actualizar la plataforma actual
                break;
            }
        }
    }

    private void checkLadderCollisions() {
        float playerHeightDiff = targetPlayer.getYPos() - yPos;


        if (Math.abs(playerHeightDiff) > PLAYER_DETECTION_RANGE) {
            List<Ladder> validLadders = gameScreen.getTestGame().getLadders().stream()
                    .filter(this::canUseLadder)
                    .toList();

            if (!validLadders.isEmpty()) {
                // Encontrar la escalera más cercana en la dirección correcta
                Ladder bestLadder = validLadders.stream()
                        .min((l1, l2) -> {
                            float d1 = Math.abs(l1.getXPos() - xPos);
                            float d2 = Math.abs(l2.getXPos() - xPos);
                            return Float.compare(d1, d2);
                        })
                        .orElse(null);

                if (bestLadder != null) {
                    isOnLadder = true;
                    currentLadder = bestLadder;
                    velocityY = 0;
                }
            }
        }
    }

    private Platform findPlatformAbove() {
        return gameScreen.getTestGame().getPlatforms().stream()
                .filter(platform -> platform.getYPos() <= yPos && // Buscar plataformas por encima o al mismo nivel
                        platform.getXPos() < xPos + width &&
                        platform.getXPos() + platform.getWidth() > xPos)
                .min(Comparator.comparingDouble(platform -> yPos - platform.getYPos()))
                .orElse(null);
    }

    private boolean isCollidingWithPlatform(Platform platform) {
        if (isOnLadder && currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            return false;
        }


        Hitbox fireHitbox = this.getDefaultHitbox();
        Hitbox platformHitbox = platform.getDefaultHitbox();

        if (fireHitbox == null || platformHitbox == null) {
            return false;
        }

        return fireHitbox.intersects(platformHitbox) &&
                Math.abs((fireHitbox.yPos() + fireHitbox.height()) - platformHitbox.yPos()) <= Math.abs(velocityY);
    }

    private boolean canUseLadder(Ladder ladder) {
        Hitbox fireHitbox = getHitboxByName(HITBOX_LADDER);
        Hitbox ladderHitbox = ladder.getDefaultHitbox();


        if (fireHitbox == null || ladderHitbox == null) return false;

        return fireHitbox.intersects(ladderHitbox);
    }

    private Platform findStepPlatform(List<Platform> platforms) {
        float checkX = velocityX > 0 ? xPos + width + 1 : xPos - 1;
// Actualizar la plataforma actual
        currentPlatform = getCurrentPlatform(platforms);


        if (currentPlatform == null) return null;

        return platforms.stream()
                .filter(platform -> isValidStepPlatform(platform, currentPlatform, checkX))
                .findFirst()
                .orElse(null);
    }

    private boolean isValidStepPlatform(Platform platform, Platform currentPlatform, float checkX) {
        if (!(checkX >= platform.getXPos() && checkX <= platform.getXPos() + platform.getWidth())) {
            return false;
        }


        if (velocityX > 0) {
            return platform.getYPos() == currentPlatform.getYPos() - 1 &&
                    Math.abs(platform.getXPos() - (currentPlatform.getXPos() + currentPlatform.getWidth())) < 0.5f;
        } else {
            return platform.getYPos() == currentPlatform.getYPos() - 1 &&
                    Math.abs(platform.getXPos() + platform.getWidth() - currentPlatform.getXPos()) < 0.5f;
        }
    }

    private Platform getCurrentPlatform(List<Platform> platforms) {
        return platforms.stream()
                .filter(this::isCollidingWithPlatform)
                .findFirst()
                .orElse(null);
    }

    private void exitLadder() {
        isOnLadder = false;
        currentLadder = null;

        // Ajustar la posición para evitar colisiones con la plataforma inmediatamente después de salir
        Platform platform = findPlatformAbove();
        if (platform != null) {
            yPos = platform.getYPos() - height; // Reposicionar el fuego sobre la plataforma
        }

        // Dar un pequeño impulso horizontal para evitar quedar atrapado
        xPos += velocityX > 0 ? 2 : -2;
        velocityY = 0;
    }

    @Override
    public void render(DrawContext context) {
        int xOffset = 0;
        int yOffset = 0;
        Animation currentAnimation = velocityX > 0 ? horizontalRightAnimation : horizontalLeftAnimation;

        renderTexture(context, this, currentAnimation, xOffset, yOffset);

        super.render(context);
    }
}