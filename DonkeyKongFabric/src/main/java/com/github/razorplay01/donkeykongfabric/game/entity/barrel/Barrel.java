package com.github.razorplay01.donkeykongfabric.game.entity.barrel;

import com.github.razorplay01.donkeykongfabric.DonkeyKongFabric;
import com.github.razorplay01.donkeykongfabric.game.entity.Entity;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Ladder;
import com.github.razorplay01.donkeykongfabric.game.mapobject.Platform;
import com.github.razorplay01.donkeykongfabric.game.util.Animation;
import com.github.razorplay01.donkeykongfabric.game.util.records.Hitbox;
import com.github.razorplay01.donkeykongfabric.game.util.ScreenSide;
import com.github.razorplay01.donkeykongfabric.screen.GameScreen;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.*;

@Getter
public class Barrel extends Entity {
    private static final String HITBOX_LADDER = "ladder";
    private static final String HITBOX_PLAYER = "player";
    private static final Identifier TEXTURE = Identifier.of(DonkeyKongFabric.MOD_ID, "textures/gui/game/barrel.png");

    private final Animation horizontalRightAnimation = new Animation(0, 0, 12, 12, 4, 0.5f, false, true);
    private final Animation horizontalLeftAnimation = new Animation(0, 12, 12, 12, 4, 0.5f, false, true);
    private final Animation verticalAnimation = new Animation(48, 0, 17, 12, 2, 0.8f, true, true);

    private static final float LADDER_DESCENT_SPEED = 0.5f;
    private static final float PROBABILITY_TO_USE_LADDER = 0.25f;
    private static final float ALIGNMENT_TOLERANCE = 4f;
    private static final float PLATFORM_DETECTION_THRESHOLD = 4f;

    private final Random random = new Random(System.currentTimeMillis());
    private final Set<Ladder> checkedLadders = new HashSet<>();

    private boolean remove = false;
    private boolean shouldChangeDirectionAfterLadder;
    private boolean hasJumpedOverBarrel = false;

    public Barrel(float x, float y, GameScreen gameScreen) {
        super(x, y, 12, 12, gameScreen, 0xFF8a3a00);
        this.velocityX = 0.6f;

        this.hitboxes.add(new Hitbox(HITBOX_LADDER, xPos - 2, yPos - 2, 16, 16, -2, -2, 0x5000FF00));
        this.hitboxes.add(new Hitbox(HITBOX_PLAYER, xPos + (this.width - 8) / 2, yPos + (this.height - 8) / 2, 8, 8, (this.width - 8) / 2, (this.height - 8) / 2, 0x50f10000));
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
            Platform nextPlatform = findNextPlatform(gameScreen.getTestGame().getPlatforms());

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
        velocityY += gravity;
        velocityY = Math.min(velocityY, maxFallSpeed);

        yPos += velocityY;
        xPos += velocityX;

        for (Platform platform : gameScreen.getTestGame().getPlatforms()) {
            if (isCollidingWithPlatform(platform)) {
                yPos = platform.getYPos() - height;
                velocityY = 0;
                checkLadderCollision(gameScreen.getTestGame().getLadders());
                break;
            }
        }
        if (velocityX > 0) {
            horizontalRightAnimation.update();
        } else {
            horizontalLeftAnimation.update();
        }
    }

    @Override
    protected void onScreenBoundaryCollision(ScreenSide side) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            if (this.yPos > gameScreen.getTestGame().getPlayer().getYPos() + 32) {
                remove = true;
            } else {
                this.velocityX *= -1;
                this.xPos = Math.clamp(xPos, gameScreen.getTestGame().getScreen().getScreenXPos(), gameScreen.getTestGame().getScreen().getScreenXPos() + gameScreen.getTestGame().getScreenWidth() - width);
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
        Hitbox barrelDefaultHitbox = this.getDefaultHitbox();
        Hitbox platformHitbox = platform.getDefaultHitbox();
        if (barrelDefaultHitbox == null || platformHitbox == null) {
            return false;
        }
        if (barrelDefaultHitbox.intersects(platformHitbox)) {
            float barrelBottom = barrelDefaultHitbox.yPos() + barrelDefaultHitbox.height();
            float platformTop = platformHitbox.yPos();
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
        Hitbox barrelLadderHitbox = barrel.getHitboxByName(HITBOX_LADDER);
        Hitbox ladderHitbox = ladder.getDefaultHitbox();
        if (barrelLadderHitbox == null || ladderHitbox == null) {
            return false;
        }
        if (!barrelLadderHitbox.intersects(ladderHitbox)) {
            return false;
        }
        float barrelCenterX = barrelLadderHitbox.xPos() + barrelLadderHitbox.width() / 2;
        float ladderCenterX = ladderHitbox.xPos() + ladderHitbox.width() / 2;
        float horizontalAlignment = Math.abs(barrelCenterX - ladderCenterX);
        return horizontalAlignment <= alignmentTolerance;
    }

    public void onJumpedOver() {
        hasJumpedOverBarrel = true;
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

        if (isOnLadder) {
            currentAnimation = verticalAnimation;
            xOffset = -2;
        } else if (velocityX > 0) {
            currentAnimation = horizontalRightAnimation;
        } else {
            currentAnimation = horizontalLeftAnimation;
        }

        context.drawTexture(
                TEXTURE,
                (int) (xPos + xOffset),
                (int) yPos,
                currentAnimation.getCurrentU(),
                currentAnimation.getCurrentV(),
                currentAnimation.getFrameWidth(),
                currentAnimation.getFrameHeight(),
                64,
                24
        );

        // Renderizar hitboxes si es necesario
        super.render(context);
    }
}