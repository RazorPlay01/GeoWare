package com.github.razorplay01.donkeykongfabric.game.entity.player;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.mapobject.VictoryZone;
import com.github.razorplay01.donkeykongfabric.game.entity.barrel.Barrel;
import com.github.razorplay01.donkeykongfabric.game.entity.Entity;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.util.*;
import com.github.razorplay01.donkeykongfabric.game.util.records.Hitbox;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Player extends Entity {
    public static final String HITBOX_LADDER = "ladder";
    public static final String HITBOX_BARREL = "barrel";

    private final List<FloatingText> floatingTexts = new ArrayList<>();

    private final PlayerAnimationManager animationManager;

    private static final Identifier TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/player.png");
    private PlayerState currentState = PlayerState.IDLE;
    private final Animation walkAnimationR = new Animation(36, 18, 18, 18, 2, 0.2f, true, true);
    private final Animation walkAnimationL = new Animation(54, 18, 18, 18, 2, 0.2f, true, true);
    private final Animation climbAnimation = new Animation(0, 0, 18, 18, 2, 0.1f, false, true);
    private final Animation idleAnimationR = new Animation(0, 36, 18, 18, 1, 0.1f, false, true);
    private final Animation idleAnimationL = new Animation(18, 36, 18, 18, 1, 0.1f, false, true);
    private final Animation jumpAnimationR = new Animation(36, 0, 18, 18, 1, 0.1f, false, true);
    private final Animation jumpAnimationL = new Animation(54, 0, 18, 18, 1, 0.1f, false, true);
    private final Animation winAnimationR = new Animation(0, 18, 18, 18, 1, 0.1f, false, true);
    private final Animation winAnimationL = new Animation(18, 18, 18, 18, 1, 0.1f, false, true);
    private final Animation loseAnimation = new Animation(0, 54, 18, 18, 4, 0.5f, false, false);

    // Variables para animación
    private boolean facingRight = true;
    private boolean isWinning = false;
    private boolean isLosing = false;

    // Estados de movimiento
    private boolean isJumping;
    private boolean movingLeft;
    private boolean movingRight;
    private boolean movingUp;
    private boolean movingDown;
    private boolean isClimbing;

    // Puntuación del jugador
    private int score = 0;
    private boolean isBarrelJumpProcessed = false;

    public Player(float x, float y, GameScreen gameScreen) {
        super(x, y, 12, 16, gameScreen, 0xFF8300ff);
        this.speed = 1.5f;
        this.gravity = 0.2f;
        this.maxFallSpeed = 4f;

        this.hitboxes.add(new Hitbox(
                HITBOX_LADDER,
                xPos,
                yPos + (this.getHeight() - 2), // Casi al final del jugador
                this.getWidth(),
                2, // Altura muy pequeña para detección precisa
                0,
                (this.getHeight() - 2),
                0xAA00ffec
        ));
        this.hitboxes.add(new Hitbox(HITBOX_BARREL, xPos + 2, yPos + 2, this.getWidth() - 4, this.getHeight() - 4, 2, 2, 0x50FF0000));
        initializeStates();
        this.animationManager = new PlayerAnimationManager();
    }

    private void initializeStates() {
        this.isJumping = false;
        this.isOnLadder = false;
        this.isClimbing = false;
        this.movingLeft = false;
        this.movingRight = false;
        this.movingUp = false;
        this.movingDown = false;
    }

    @Override
    public void update() {
        animationManager.updateAnimation(currentState, movingUp || movingDown || movingLeft || movingRight);

        if (isWinning || isLosing) {
            updateState();
            return;
        }

        updateMovement();
        updateState();
        updateHitboxes();

        checkPlatformCollision(gameScreen.getTestGame().getPlatforms());
        verifyScreenBoundsCollision();
        checkLadderContact();
        checkBarrelCollision(gameScreen.getTestGame().getBarrels());
    }

    private void updateMovement() {
        if (isOnLadder) {
            updateLadderMovement();
        } else {
            updateNormalMovement();
            xPos += velocityX;
            yPos += velocityY;
        }
    }

    private void updateNormalMovement() {
        // Movimiento horizontal
        velocityX = 0;
        if (movingLeft && !movingRight) {
            velocityX = -speed;
            animationManager.setFacingDirection(false);
        } else if (movingRight && !movingLeft) {
            velocityX = speed;
            animationManager.setFacingDirection(true);
        }

        // Aplicar gravedad si no está en escalera
        if (!isOnLadder) {
            velocityY = Math.min(velocityY + gravity, maxFallSpeed);
        }
    }

    private void updateLadderMovement() {
        if (currentLadder == null) return;
        float playerClimbSpeed = 1f;

        velocityX = 0;
        velocityY = 0;

        if (movingUp) {
            yPos -= playerClimbSpeed;
            isClimbing = true;
        } else if (movingDown) {
            if (PlayerCollisionHandler.canContinueClimbingDown(this, currentLadder, gameScreen.getTestGame().getPlatforms())) {
                yPos += playerClimbSpeed;
                isClimbing = true;
            } else {
                stopClimbing();
            }
        } else {
            isClimbing = false;
        }

        if (currentLadder != null) {
            this.xPos = currentLadder.getXPos() + (currentLadder.getWidth() - this.getWidth()) / 2;
        }
    }

    public void jump(int screenHeight) {
        if (!isJumping && (currentPlatform != null || yPos + this.getHeight() >= screenHeight - 1)) {
            velocityY = -2.6f;
            isJumping = true;
            currentPlatform = null;
        }
    }

    public void moveLeft() {
        if (!isWinning) {
            movingLeft = true;
            stopClimbing();
            animationManager.setFacingDirection(false);
        }
    }

    public void moveRight() {
        if (!isWinning) {
            movingRight = true;
            stopClimbing();
            animationManager.setFacingDirection(true);
        }
    }

    public void moveUp(List<Ladder> ladders) {
        movingUp = true;
        movingDown = false;
        if (!tryClimbLadder(ladders) && !isOnLadder) {
            jump(gameScreen.getTestGame().getScreenHeight());
        }
    }

    public void moveDown(List<Ladder> ladders) {
        if (!isWinning) {
            movingDown = true;
            movingUp = false;
            tryClimbLadder(ladders);
        }
    }

    public void stopMovingLeft() {
        movingLeft = false;
    }

    public void stopMovingRight() {
        movingRight = false;
    }

    public void stopMovingUp() {
        movingUp = false;
    }

    public void stopMovingDown() {
        movingDown = false;
    }

    private void startClimbing() {
        if (currentLadder != null) {
            isOnLadder = true;
            isClimbing = true;
            velocityX = 0;
            velocityY = 0;
        }
    }

    public void stopClimbing() {
        if (isOnLadder) {
            velocityY = 0;
            currentPlatform = null;
        }

        isOnLadder = false;
        isClimbing = false;
        currentLadder = null;
        movingUp = false;
        movingDown = false;
    }

    private void checkLadderContact() {
        if (isOnLadder && currentLadder != null && !PlayerCollisionHandler.isValidLadderContact(this, currentLadder)) {
            stopClimbing();
        }
    }

    // Métodos de manejo de escaleras
    private boolean tryClimbLadder(List<Ladder> ladders) {
        Ladder bestLadder = PlayerCollisionHandler.findBestLadder(this, ladders, movingUp);

        if (bestLadder != null) {
            if (movingDown && !bestLadder.isCanPassThroughPlatform()) {
                return false;
            }

            if (movingUp || movingDown) {
                currentLadder = bestLadder;
                startClimbing();
                return true;
            }
        }
        return false;
    }

    private void checkBarrelCollision(List<Barrel> barrels) {
        if (currentState == PlayerState.WINNING || currentState == PlayerState.LOSING) {
            return;
        }

        PlayerCollisionHandler.CollisionResult result = PlayerCollisionHandler.handleBarrelCollision(this, barrels);

        if (result.hasCollision()) {
            if (result.isJumpOver()) {
                if (!isBarrelJumpProcessed) {
                    addScore(100);
                    isBarrelJumpProcessed = true;
                }
            } else {
                setLosing(true);
            }
        } else {
            if (currentState != PlayerState.JUMPING) {
                isBarrelJumpProcessed = false;
                PlayerCollisionHandler.resetBarrelJumpStates(barrels);
            }
        }
    }

    private void checkPlatformCollision(List<Platform> platforms) {
        if (isOnLadder && currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            return;
        }

        PlayerCollisionHandler.CollisionPoints points = new PlayerCollisionHandler.CollisionPoints(
                xPos + (this.getWidth() / 2),
                yPos + this.getHeight(),
                yPos - velocityY + this.getHeight(),
                xPos + 2,
                xPos + this.getWidth() - 2
        );

        Platform mainPlatform = PlayerCollisionHandler.findMainPlatform(this, platforms, points);
        Platform stepPlatform = null;

        if (mainPlatform == null && currentPlatform != null && Math.abs(velocityX) > 0) {
            stepPlatform = PlayerCollisionHandler.findStepPlatform(this, platforms, points);
        }

        if (mainPlatform != null) {
            resolveCollision(mainPlatform);
        } else if (stepPlatform != null) {
            resolveCollision(stepPlatform);
        } else if (!isClimbing) {
            currentPlatform = null;
        }
    }

    public void checkVictoryPlatform(VictoryZone victoryPlatform) {
        if (!isWinning && !isLosing && PlayerCollisionHandler.checkVictoryCondition(this, victoryPlatform)) {
            isWinning = true;
        }
    }

    public void setLosing(boolean losing) {
        this.isLosing = losing;
        if (losing) {
            velocityX = 0;
            velocityY = 0;
        }
    }

    private void resolveCollision(Platform platform) {
        yPos = platform.getYPos() - this.getHeight();
        velocityY = 0;
        isJumping = false;
        currentPlatform = platform;
    }

    @Override
    public void render(DrawContext context) {
        this.animationManager.render(context, this.xPos, this.yPos);

        floatingTexts.removeIf(text -> !text.isActive());
        for (FloatingText text : floatingTexts) {
            text.render(context, gameScreen.getTestGame().getTextRenderer());
        }
        super.render(context);
    }

    /**
     * Añade puntos al score yPos muestra un texto flotante con la cantidad
     *
     * @param points Cantidad de puntos a añadir
     */
    public void addScore(int points) {
        score += points;
        float textX = xPos + (this.getWidth() / 2);
        float textY = yPos;
        String scoreText = "" + points;
        floatingTexts.add(new FloatingText(textX, textY, scoreText, 0.5f));
    }

    private void updateState() {
        PlayerState previousState = currentState;

        if (isWinning) {
            currentState = PlayerState.WINNING;
        } else if (isLosing) {
            currentState = PlayerState.LOSING;
        } else if (isOnLadder) {
            currentState = PlayerState.CLIMBING;
        } else if (isJumping) {
            currentState = PlayerState.JUMPING;
        } else if (Math.abs(velocityX) > 0) {
            currentState = PlayerState.WALKING;
        } else {
            currentState = PlayerState.IDLE;
        }

        if (previousState != currentState) {
            onStateChange(currentState);
        }
    }

    private void onStateChange(PlayerState newState) {
        if (newState == PlayerState.JUMPING) {
            velocityY = -2.6f;
            currentPlatform = null;
        } else if (newState == PlayerState.CLIMBING || newState == PlayerState.WINNING || newState == PlayerState.LOSING) {
            velocityX = 0;
            velocityY = 0;
        }
    }
}