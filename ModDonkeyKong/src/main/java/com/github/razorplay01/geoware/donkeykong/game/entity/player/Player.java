package com.github.razorplay01.geoware.donkeykong.game.entity.player;

import com.github.razorplay01.geoware.donkeykong.game.entity.Particle;
import com.github.razorplay01.geoware.donkeykong.game.entity.item.HammetItem;
import com.github.razorplay01.geoware.donkeykong.game.entity.item.ItemEntity;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.geoware.donkeykong.game.entity.barrel.Barrel;
import com.github.razorplay01.geoware.donkeykong.game.entity.Entity;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geoware.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geoware.donkeykong.game.stages.Game;
import com.github.razorplay01.geoware.donkeykong.game.util.Animation;
import com.github.razorplay01.geoware.donkeykong.game.util.FloatingText;
import com.github.razorplay01.geoware.donkeykong.game.util.records.Hitbox;
import com.github.razorplay01.geoware.donkeykong.screen.GameScreen;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.razorplay01.geoware.donkeykong.game.util.records.Hitbox.*;
import static com.github.razorplay01.geoware.donkeykong.game.util.texture.TextureProvider.PARTICLE_TEXTURES;

@Setter
@Getter
public class Player extends Entity {
    private final List<FloatingText> floatingTexts = new ArrayList<>();
    private final PlayerAnimationManager animationManager;
    private PlayerState currentState = PlayerState.IDLE;

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

    private boolean hasHammer = false;
    private long hammerStartTime;
    private static final long HAMMER_DURATION = 10000; // 10 segundos en millisegundos
    private static final String HITBOX_HAMMER = "hammer";
    private static final String HITBOX_HAMMER_TOP = "hammer_top";

    public Player(float x, float y, GameScreen gameScreen) {
        super(x, y, 12, 16, gameScreen, 0xFF8300ff);
        this.speed = 1f;
        this.gravity = 0.2f;
        this.maxFallSpeed = 4f;

        this.hitboxes.add(new Hitbox(HITBOX_LADDER, xPos, yPos + (this.getHeight() - 2), this.getWidth(), 2, 0, (this.getHeight() - 2), LADDER_HITBOX_COLOR));
        this.hitboxes.add(new Hitbox(HITBOX_BARREL, xPos + 2, yPos + 2, this.getWidth() - 4, this.getHeight() - 4, 2, 2, PLAYER_HITBOX_COLOR));

        initializeStates();
        this.animationManager = new PlayerAnimationManager(this);
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

        if (hasHammer) {
            updateHammer();
        }

        updateMovement();
        updateState();
        updateHitboxes();

        checkPlatformCollision(Game.platforms);
        verifyScreenBoundsCollision();
        checkLadderContact();
        checkBarrelCollision(gameScreen.getTestGame().getBarrels());
        checkHammerItemCollision(gameScreen.getTestGame().getItems());
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
        float playerClimbSpeed = 0.6f;

        velocityX = 0;
        velocityY = 0;

        if (movingUp) {
            yPos -= playerClimbSpeed;
            isClimbing = true;
        } else if (movingDown) {
            if (PlayerCollisionHandler.canContinueClimbingDown(this, currentLadder, Game.platforms)) {
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
        if (hasHammer) return;
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
        if (hasHammer) return;
        movingUp = true;
        movingDown = false;
        tryClimbLadder(ladders);
    }

    public void moveDown(List<Ladder> ladders) {
        if (hasHammer) return;
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
        if (hasHammer) return false;
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

        // Primero verificamos si hay colisión con el martillo
        if (hasHammer) {
            checkHammerBarrelCollision(barrels);
        }

        PlayerCollisionHandler.CollisionResult result = PlayerCollisionHandler.handleBarrelCollision(this, barrels);

        if (result.hasCollision()) {
            if (result.isJumpOver()) {
                if (!isBarrelJumpProcessed) {
                    addScore(100);
                    isBarrelJumpProcessed = true;
                }
            } else {
                //TODO: disable for test
                //setLosing(true);
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
        this.animationManager.render(context, this);

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
        } else if (hasHammer) {
            currentState = PlayerState.WITH_HAMMER;
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

    private void activateHammer() {
        hasHammer = true;
        hammerStartTime = System.currentTimeMillis();
        currentState = PlayerState.WITH_HAMMER;

        if (isOnLadder) {
            stopClimbing();
        }

        addHammerHitbox();
    }

    private void updateHammer() {
        if (System.currentTimeMillis() - hammerStartTime >= HAMMER_DURATION) {
            deactivateHammer();
            return;
        }

        // Actualizar posición del hitbox del martillo
        removeHammerHitbox();
        addHammerHitbox();
    }

    private void addHammerHitbox() {
        // Hitbox frontal
        Hitbox frontHammerHitbox;
        // Hitbox superior
        Hitbox topHammerHitbox;

        if (animationManager.isFacingRight()) {
            frontHammerHitbox = new Hitbox(HITBOX_HAMMER,
                    xPos + getWidth(),
                    yPos,
                    16, 8,
                    getWidth(), 0,
                    0xAAFFAAFF);

            topHammerHitbox = new Hitbox(HITBOX_HAMMER_TOP,
                    xPos,
                    yPos - 8,
                    getWidth(),
                    8,
                    0,
                    -8,
                    0xAAFFAAFF);
        } else {
            frontHammerHitbox = new Hitbox(HITBOX_HAMMER,
                    xPos - 16,
                    yPos,
                    16, 8,
                    -16, 0,
                    0xAAFFAAFF);

            topHammerHitbox = new Hitbox(HITBOX_HAMMER_TOP,
                    xPos,
                    yPos - 8,
                    getWidth(),
                    8,
                    0,
                    -8,
                    0xAAFFAAFF);
        }

        this.hitboxes.add(frontHammerHitbox);
        this.hitboxes.add(topHammerHitbox);
    }

    private void deactivateHammer() {
        hasHammer = false;
        removeHammerHitbox();
        currentState = PlayerState.IDLE;
    }

    private void removeHammerHitbox() {
        hitboxes.removeIf(hitbox ->
                hitbox.name().equals(HITBOX_HAMMER) ||
                        hitbox.name().equals(HITBOX_HAMMER_TOP)
        );
    }

    private void checkHammerBarrelCollision(List<Barrel> barrels) {
        Hitbox frontHammerHitbox = getHitboxByName(HITBOX_HAMMER);
        Hitbox topHammerHitbox = getHitboxByName(HITBOX_HAMMER_TOP);

        for (Barrel barrel : barrels) {
            Hitbox barrelHitbox = barrel.getDefaultHitbox();
            if (barrelHitbox == null) continue;

            if ((frontHammerHitbox != null && frontHammerHitbox.intersects(barrelHitbox)) ||
                    (topHammerHitbox != null && topHammerHitbox.intersects(barrelHitbox))) {

                float particleX = barrel.getXPos() + (barrel.getWidth() - 16) / 2;
                float particleY = barrel.getYPos() + (barrel.getHeight() - 16) / 2;
                gameScreen.getTestGame().getParticles().add(
                        new Particle(particleX, particleY, 16, 16, gameScreen,
                                new Animation(PARTICLE_TEXTURES, 0.5f, false)));

                barrel.setRemove(true);
                addScore(200);
            }
        }
    }

    private void checkHammerItemCollision(List<ItemEntity> hammers) {
        Hitbox playerHitbox = getHitboxByName(HITBOX_BARREL);
        if (playerHitbox == null) return;

        Iterator<ItemEntity> iterator = hammers.iterator();
        while (iterator.hasNext()) {
            ItemEntity item = iterator.next();
            if (item instanceof HammetItem hammer) {
                Hitbox hammerHitbox = hammer.getDefaultHitbox();
                if (hammerHitbox != null && playerHitbox.intersects(hammerHitbox) && !hasHammer) {
                    activateHammer();
                    iterator.remove();
                }
            }
        }
    }
}