package com.github.razorplay01.geowaremod.donkeykong.game.entity.barrel;

import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geowaremod.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.razorplayapi.util.ScreenSide;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import com.github.razorplay01.razorplayapi.util.texture.Animation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.util.*;

import static com.github.razorplay01.geowaremod.donkeykong.game.util.TextureProvider.*;
import static com.github.razorplay01.geowaremod.donkeykong.DonkeyKongGame.*;

@Getter
public class Barrel extends com.github.razorplay01.geowaremod.donkeykong.game.entity.DonkeyKongEntity {
    private final Animation horizontalRightAnimation = new Animation(BARREL_R_TEXTURES, 0.8f, true);
    private final Animation horizontalLeftAnimation = new Animation(BARREL_L_TEXTURES, 0.8f, true);
    private final Animation verticalAnimation = new Animation(BARREL_V_TEXTURES, 1f, true);

    private static final float LADDER_DESCENT_SPEED = 0.5f;
    private static final float PROBABILITY_TO_USE_LADDER = 0.25f;
    private static final float ALIGNMENT_TOLERANCE = 4f;
    private static final float PLATFORM_DETECTION_THRESHOLD = 4f;
    private static final float PLATFORM_SEGMENT_WIDTH = 16f;  // Cada segmento de plataforma es de 16 píxeles
    private static final float SLOPE_Y = 1f;  // Pendiente confirmada: 1 píxel por segmento

    private final Random random = new Random(System.currentTimeMillis());
    private final Set<Ladder> checkedLadders = new HashSet<>();

    @Setter
    private boolean remove = false;
    private boolean shouldChangeDirectionAfterLadder;
    private boolean hasJumpedOverBarrel = false;

    public Barrel(float x, float y, GameScreen gameScreen) {
        super(x, y, 12, 12, gameScreen,0xffffaaff);
        this.velocityX = 1f;  // Restaurado para que ruede hacia la derecha inicialmente

        this.hitboxes.add(new RectangleHitbox(HITBOX_LADDER, xPos - 2, yPos - 2, 16, 16, -2, -2, LADDER_HITBOX_COLOR));
        this.hitboxes.add(new RectangleHitbox(HITBOX_PLAYER, xPos + (this.width - 8) / 2, yPos + (this.height - 8) / 2, 8, 8, (this.width - 8) / 2, (this.height - 8) / 2, PLAYER_HITBOX_COLOR));
    }

    @Override
    public void update() {
        if (isOnLadder) {
            handleLadderMovement();
        } else {
            handleNormalMovement();
        }
        updateHitboxes();
        verifyScreenBoundsCollision();
    }

    private void handleLadderMovement() {
        if (currentLadder == null) return;

        yPos += LADDER_DESCENT_SPEED;
        xPos = currentLadder.getXPos() + (currentLadder.getWidth() - width) / 2;

        if (yPos + height >= currentLadder.getYPos() + currentLadder.getHeight() - PLATFORM_DETECTION_THRESHOLD) {
            Platform nextPlatform = findNextPlatform(game.getPlatforms());

            if (nextPlatform != null && Math.abs(nextPlatform.getYPos() - (yPos + height)) <= PLATFORM_DETECTION_THRESHOLD) {
                yPos = nextPlatform.getYPos() - height;
                exitLadder();
            } else {
                exitLadder();
            }
        }
        verticalAnimation.update();
    }

    private void exitLadder() {
        isOnLadder = false;
        currentLadder = null;
        velocityY = 0.1f;

        if (shouldChangeDirectionAfterLadder) {
            velocityX = -velocityX;
            shouldChangeDirectionAfterLadder = false;
        }
    }

    private void handleNormalMovement() {
        // Aplicar gravedad y mover verticalmente
        velocityY += gravity;
        velocityY = Math.min(velocityY, maxFallSpeed);
        yPos += velocityY;

        // Mover horizontalmente
        xPos += velocityX;

        // Verificar colisión con plataformas
        Platform platformBeneath = null;
        for (Platform platform : game.getPlatforms()) {
            if (isCollidingWithPlatform(platform)) {
                platformBeneath = platform;
                break;
            }
        }

        if (platformBeneath != null) {
            // Ajustar posición vertical según la inclinación
            float platformStartX = platformBeneath.getXPos();
            float platformStartY = platformBeneath.getYPos();
            float relativeX = xPos - platformStartX;
            int segment = (int) (relativeX / PLATFORM_SEGMENT_WIDTH);  // Determinar en qué segmento está el barril
            float slopeAdjustment = segment * SLOPE_Y;  // Ajuste vertical basado en la inclinación

            float effectiveSlopeY = SLOPE_Y;
            if (platformBeneath.getXPos() > 100f) {  // Hack temporal: asumimos que plataformas que empiezan a la derecha (como createThirdPlatform) tienen inclinación inversa
                effectiveSlopeY = -SLOPE_Y;
            }
            slopeAdjustment = segment * effectiveSlopeY;

            yPos = platformStartY - height + slopeAdjustment;
            velocityY = 0;  // Restablecer velocidad vertical para evitar vibración
            currentPlatform = platformBeneath;
            checkLadderCollision(game.getLadders());
        } else {
            currentPlatform = null;
        }

        // Actualizar animación según la dirección
        if (velocityX > 0) {
            horizontalRightAnimation.update();
        } else {
            horizontalLeftAnimation.update();
        }
    }

    protected void verifyScreenBoundsCollision() {
        int screenX = gameScreen.getGame().getScreen().getGameScreenXPos();
        int screenY = gameScreen.getGame().getScreen().getGameScreenYPos();
        int screenWidth = gameScreen.getGame().getScreenWidth();
        int screenHeight = gameScreen.getGame().getScreenHeight();

        if (xPos < screenX) {
            xPos = screenX;
            if (velocityY <= 0) {  // Solo cambiar dirección si no está cayendo
                onScreenBoundaryCollision(ScreenSide.LEFT);
            }
            return;
        }
        if (xPos + width > screenX + screenWidth) {
            xPos = screenX + screenWidth - width;
            if (velocityY <= 0) {
                onScreenBoundaryCollision(ScreenSide.RIGHT);
            }
            return;
        }
        if (yPos < screenY) {
            yPos = screenY;
            onScreenBoundaryCollision(ScreenSide.TOP);
            return;
        }
        if (yPos + height > screenY + screenHeight) {
            yPos = screenY + screenHeight - height;
            onScreenBoundaryCollision(ScreenSide.BOTTOM);
        }
    }

    protected void onScreenBoundaryCollision(ScreenSide side) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            if (this.yPos > game.getPlayer().getYPos() + 32) {
                remove = true;
            } else {
                this.velocityX *= -1;
                this.xPos = Math.clamp(xPos, game.getScreen().getGameScreenXPos(), game.getScreen().getGameScreenXPos() + game.getScreenWidth() - width);
            }
        } else if (side.equals(ScreenSide.TOP) || side.equals(ScreenSide.BOTTOM)) {
            remove = true;
            velocityY = 0;
        }
    }

    private Platform findNextPlatform(List<Platform> platforms) {
        Platform closest = null;
        float minDistance = Float.MAX_VALUE;
        float barrelBottom = yPos + height;

        for (Platform platform : platforms) {
            if (platform.getYPos() > barrelBottom) { // Solo plataformas debajo del barril
                float distance = platform.getYPos() - barrelBottom;
                if (distance < minDistance &&
                        xPos + width > platform.getXPos() &&
                        xPos < platform.getXPos() + platform.getWidth()) {
                    minDistance = distance;
                    closest = platform;
                }
            }
        }
        return closest;
    }

    private boolean isCollidingWithPlatform(Platform platform) {
        if (isOnLadder && currentLadder != null && currentLadder.isCanPassThroughPlatform()) {
            return false;
        }
        RectangleHitbox barrelDefaultHitbox = (RectangleHitbox) this.getDefaultHitbox();
        Hitbox platformHitbox = platform.getDefaultHitbox();
        if (barrelDefaultHitbox == null || platformHitbox == null) {
            return false;
        }
        if (barrelDefaultHitbox.intersects(platformHitbox)) {
            float barrelBottom = barrelDefaultHitbox.getYPos() + barrelDefaultHitbox.getHeight();
            float platformTop = platformHitbox.getYPos();
            return Math.abs(barrelBottom - platformTop) <= Math.abs(velocityY);
        }
        return false;
    }

    private void checkLadderCollision(List<Ladder> ladders) {
        List<Ladder> validLadders = new ArrayList<>();
        Hitbox barrelLadderHitbox = getHitboxByName(HITBOX_LADDER);
        if (barrelLadderHitbox == null) {
            return;
        }
        for (Ladder ladder : ladders) {
            if (!checkedLadders.contains(ladder) && ladder.isCanPassThroughPlatform() && canUseLadder(this, ladder, ALIGNMENT_TOLERANCE)) {
                validLadders.add(ladder);
            }
        }
        if (!validLadders.isEmpty()) {
            Ladder selectedLadder = validLadders.get(random.nextInt(validLadders.size()));
            checkedLadders.add(selectedLadder);
            if (random.nextFloat() < PROBABILITY_TO_USE_LADDER) {
                isOnLadder = true;
                currentLadder = selectedLadder;
                velocityY = 0;
                shouldChangeDirectionAfterLadder = true;
                xPos = selectedLadder.getXPos() + (selectedLadder.getWidth() - width) / 2;
            }
        }
    }

    public boolean canUseLadder(Barrel barrel, Ladder ladder, float alignmentTolerance) {
        RectangleHitbox barrelLadderHitbox = (RectangleHitbox) barrel.getHitboxByName(HITBOX_LADDER);
        RectangleHitbox ladderHitbox = (RectangleHitbox) ladder.getDefaultHitbox();
        if (barrelLadderHitbox == null || ladderHitbox == null) {
            return false;
        }
        if (!barrelLadderHitbox.intersects(ladderHitbox)) {
            return false;
        }
        float barrelCenterX = barrelLadderHitbox.getXPos() + barrelLadderHitbox.getWidth() / 2;
        float ladderCenterX = ladderHitbox.getXPos() + ladderHitbox.getWidth() / 2;
        float horizontalAlignment = Math.abs(barrelCenterX - ladderCenterX);
        return horizontalAlignment <= alignmentTolerance;
    }

    public boolean hasBeenJumpedOver() {
        return hasJumpedOverBarrel;
    }

    public void resetJumpState() {
        hasJumpedOverBarrel = false;
    }

    @Override
    public void render(DrawContext context) {
        Animation currentAnimation;
        int xOffset = 0;
        int yOffset = -1;

        if (isOnLadder) {
            currentAnimation = verticalAnimation;
        } else if (velocityX > 0) {
            currentAnimation = horizontalRightAnimation;
        } else {
            currentAnimation = horizontalLeftAnimation;
        }

        renderTexture(context, this, currentAnimation, xOffset, yOffset);
    }
}