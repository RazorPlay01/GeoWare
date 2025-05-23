package com.github.razorplay01.geowaremod.games.donkeykong.game.entity.player;

import com.github.razorplay01.geowaremod.GameSounds;
import com.github.razorplay01.geowaremod.games.donkeykong.game.entity.DonkeyKongEntity;
import com.github.razorplay01.geowaremod.games.donkeykong.game.entity.barrel.Barrel;
import com.github.razorplay01.geowaremod.games.donkeykong.game.entity.item.HammetItem;
import com.github.razorplay01.geowaremod.games.donkeykong.game.entity.item.ItemEntity;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.razorplayapi.util.Particle;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.Iterator;
import java.util.List;

import static com.github.razorplay01.geowaremod.games.donkeykong.game.util.TextureProvider.PARTICLE_TEXTURES;
import static com.github.razorplay01.geowaremod.games.donkeykong.DonkeyKongGame.*;
import static com.github.razorplay01.razorplayapi.util.ScreenSide.verifyScreenBoundsCollision;

@Setter
@Getter
public class Player extends DonkeyKongEntity {
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
    private boolean isBarrelJumpProcessed = false;

    private boolean hasHammer = false;
    private long hammerStartTime;
    private static final long HAMMER_DURATION = 10000; // 10 segundos en milisegundos
    private static final String HITBOX_HAMMER = "hammer";
    private static final String HITBOX_HAMMER_TOP = "hammer_top";

    public Player(float x, float y, GameScreen gameScreen) {
        super(x, y, 12, 16, gameScreen, 0xFF8300ff);
        this.speed = 2.5f;
        this.gravity = 0.3f;
        this.maxFallSpeed = 4f;

        this.hitboxes.add(new RectangleHitbox(HITBOX_LADDER, xPos, yPos + (this.getHeight() - 2), this.getWidth(), 2, 0, (this.getHeight() - 2), LADDER_HITBOX_COLOR));
        this.hitboxes.add(new RectangleHitbox(HITBOX_BARREL, xPos + 2, yPos + 2, this.getWidth() - 4, this.getHeight() - 4, 2, 2, PLAYER_HITBOX_COLOR));

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
        if (isLosing) {
            movingDown = false;
            movingLeft = false;
            movingRight = false;
            movingUp = false;
            isClimbing = false;
        }
        if (hasHammer) {
            updateHammer();
        }

        updateMovement();
        updateState();
        updateHitboxes();

        checkPlatformCollision(game.getPlatforms());
        verifyScreenBoundsCollision(this, gameScreen, (RectangleHitbox) getDefaultHitbox());
        checkLadderContact();
        checkBarrelCollision(game.getBarrels());
        checkHammerItemCollision(game.getItems());
    }

    private void updateMovement() {
        if (isWinning || isLosing) {
            velocityY = 0;
            velocityX = 0;
            return;
        }
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
        float playerClimbSpeed = 1.5f;

        velocityX = 0;
        velocityY = 0;

        if (movingUp) {
            yPos -= playerClimbSpeed;
            isClimbing = true;
        } else if (movingDown) {
            if (PlayerCollisionHandler.canContinueClimbingDown(this, currentLadder, game.getPlatforms())) {
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
        game.playSound(GameSounds.DONKEYKONG_JUMP, game.getSoundVolume(), 1.0f); // Perdió
        if (hasHammer || isLosing || isWinning) return;
        if (!isJumping && (currentPlatform != null || yPos + this.getHeight() >= screenHeight - 1)) {
            velocityY = -2.6f;
            isJumping = true;
            currentPlatform = null;
        }
    }

    public void moveLeft() {
        if (isWinning || isLosing) return;
        movingLeft = true;
        stopClimbing();
        animationManager.setFacingDirection(false);
    }

    public void moveRight() {
        if (isWinning || isLosing) return;
        movingRight = true;
        stopClimbing();
        animationManager.setFacingDirection(true);
    }

    public void moveUp(List<Ladder> ladders) {
        if (hasHammer || isLosing) return;
        movingUp = true;
        movingDown = false;
        tryClimbLadder(ladders);
    }

    public void moveDown(List<Ladder> ladders) {
        if (hasHammer || isLosing || isWinning) return;
        movingDown = true;
        movingUp = false;
        tryClimbLadder(ladders);
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
        if (currentState == PlayerState.WINNING || currentState == PlayerState.LOSING) return;

        if (hasHammer) {
            checkHammerBarrelCollision(barrels);
        }

        PlayerCollisionHandler.CollisionResult result = PlayerCollisionHandler.handleBarrelCollision(this, barrels);

        if (result.hasCollision()) {
            if (result.isJumpOver()) {
                if (!isBarrelJumpProcessed) {
                    game.addScore(1, xPos, yPos);
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

        RectangleHitbox playerHitbox = (RectangleHitbox) getDefaultHitbox();
        if (playerHitbox == null) return;

        // Primero buscamos colisión principal desde arriba
        Platform collisionPlatform = null;
        float highestPlatformY = Float.MAX_VALUE;

        for (Platform platform : platforms) {
            Hitbox platformHitbox = platform.getDefaultHitbox();
            if (platformHitbox == null) continue;

            float playerBottom = yPos + height;
            float platformTop = platform.getYPos();
            float previousBottom = yPos + height - velocityY;

            // Colisión desde arriba
            if (velocityY >= 0 && playerHitbox.intersects(platformHitbox) &&
                    previousBottom <= platformTop && playerBottom >= platformTop) {
                if (platformTop < highestPlatformY) {
                    highestPlatformY = platformTop;
                    collisionPlatform = platform;
                }
            }
        }

        if (collisionPlatform != null) {
            resolveCollision(collisionPlatform);
            return; // Si hay colisión principal, no necesitamos verificar steps
        }

        // Si no hay colisión principal, intentamos subir un desnivel (step)
        if (currentPlatform != null && Math.abs(velocityX) > 0) {
            PlayerCollisionHandler.CollisionPoints points = new PlayerCollisionHandler.CollisionPoints(
                    xPos + (this.getWidth() / 2),
                    yPos + this.getHeight(),
                    yPos - velocityY + this.getHeight(),
                    xPos + 2,
                    xPos + this.getWidth() - 2
            );
            Platform stepPlatform = PlayerCollisionHandler.findStepPlatform(this, platforms, points);
            if (stepPlatform != null) {
                resolveCollision(stepPlatform);
            } else if (!isClimbing) {
                currentPlatform = null;
            }
        } else if (!isClimbing) {
            currentPlatform = null;
        }
    }

    private boolean playedWinSound = false;

    public void checkVictoryPlatform(VictoryZone victoryPlatform) {
        if (!isWinning && !isLosing && PlayerCollisionHandler.checkVictoryCondition(this, victoryPlatform)) {
            isWinning = true;
            if (!playedWinSound) {
                game.playSound(GameSounds.DONKEYKONG_WIN, game.getSoundVolume(), 1.0f); // Perdió
            }
        }
    }

    public void setLosing(boolean losing) {
        game.playSound(GameSounds.DONKEYKONG_DEAD, game.getSoundVolume(), 1.0f); // Perdió
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
    public void render(DrawContext context, float delta) {
        animationManager.updateAnimation(delta, currentState, movingUp || movingDown || movingLeft || movingRight);
        this.animationManager.render(context, this);
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
        game.playSound(GameSounds.DONKEYKONG_MARTILLO, game.getSoundVolume(), 1.0f); // Perdió
        hasHammer = true;
        hammerStartTime = System.currentTimeMillis();
        currentState = PlayerState.WITH_HAMMER;
        if (isOnLadder) stopClimbing();
        addHammerHitbox();
    }

    private void updateHammer() {
        if (System.currentTimeMillis() - hammerStartTime >= HAMMER_DURATION) {
            deactivateHammer();
            return;
        }
        removeHammerHitbox();
        addHammerHitbox();
    }

    private void addHammerHitbox() {
        Hitbox frontHammerHitbox = animationManager.isFacingRight() ?
                new RectangleHitbox(HITBOX_HAMMER, xPos + getWidth(), yPos, 16, 8, getWidth(), 0, 0xAAFFAAFF) :
                new RectangleHitbox(HITBOX_HAMMER, xPos - 16, yPos, 16, 8, -16, 0, 0xAAFFAAFF);

        Hitbox topHammerHitbox = new RectangleHitbox(HITBOX_HAMMER_TOP, xPos, yPos - 8, getWidth(), 8, 0, -8, 0xAAFFAAFF);

        this.hitboxes.add(frontHammerHitbox);
        this.hitboxes.add(topHammerHitbox);
    }

    private void deactivateHammer() {
        hasHammer = false;
        removeHammerHitbox();
        currentState = PlayerState.IDLE;
    }

    private void removeHammerHitbox() {
        hitboxes.removeIf(hitbox -> hitbox.getHitboxId().equals(HITBOX_HAMMER) || hitbox.getHitboxId().equals(HITBOX_HAMMER_TOP));
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
                game.addParticle(new Animation(PARTICLE_TEXTURES, 4f, false), particleX, particleY, 16, 16);
                game.addScore(2, barrel.getXPos(), barrel.getYPos());
                game.playSound(GameSounds.DONKEYKONG_BARRIL_EXPLOSION, game.getSoundVolume(), 1.0f); // Perdió
                barrel.setRemove(true);
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