package com.github.razorplay01.geowaremod.games.donkeykong.game.entity.player;

import com.github.razorplay01.geowaremod.games.donkeykong.game.entity.barrel.Barrel;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.Ladder;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.Platform;
import com.github.razorplay01.geowaremod.games.donkeykong.game.mapobject.VictoryZone;
import com.github.razorplay01.razorplayapi.util.hitbox.Hitbox;
import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;

import java.util.List;

import static com.github.razorplay01.geowaremod.games.donkeykong.DonkeyKongGame.*;

public class PlayerCollisionHandler {
    private static final float BARREL_JUMP_HEIGHT = 20.0f;
    private static final float BARREL_TOP_OFFSET = 4.0f;
    private static final float PLATFORM_TOLERANCE = 0.5f;

    public static Platform findMainPlatform(Player player, List<Platform> platforms, CollisionPoints points) {
        return platforms.stream()
                .filter(platform -> isMainPlatformCollision(player, platform, points))
                .findFirst()
                .orElse(null);
    }

    private static boolean isMainPlatformCollision(Player player, Platform platform, CollisionPoints points) {
        return points.centerX() >= platform.getXPos() &&
                points.centerX() <= platform.getXPos() + platform.getWidth() &&
                player.getVelocityY() >= 0 &&
                points.previousBottom() <= platform.getYPos() + PLATFORM_TOLERANCE &&
                points.bottom() >= platform.getYPos();
    }

    public static Platform findStepPlatform(Player player, List<Platform> platforms, CollisionPoints points) {
        float checkX = player.getVelocityX() > 0 ? points.rightCheckX() : points.leftCheckX();

        return platforms.stream()
                .filter(platform -> isValidStepPlatform(player, platform, checkX))
                .findFirst()
                .orElse(null);
    }

    private static boolean isValidStepPlatform(Player player, Platform platform, float checkX) {
        Platform currentPlatform = player.getCurrentPlatform();
        if (currentPlatform == null) return false;

        if (!(checkX >= platform.getXPos() && checkX <= platform.getXPos() + platform.getWidth())) {
            return false;
        }

        if (player.getVelocityX() > 0) {
            return platform.getYPos() == currentPlatform.getYPos() - 1 &&
                    Math.abs(platform.getXPos() - (currentPlatform.getXPos() + currentPlatform.getWidth())) < PLATFORM_TOLERANCE;
        } else {
            return platform.getYPos() == currentPlatform.getYPos() - 1 &&
                    Math.abs(platform.getXPos() + platform.getWidth() - currentPlatform.getXPos()) < PLATFORM_TOLERANCE;
        }
    }

    public static boolean canContinueClimbingDown(Player player, Ladder ladder, List<Platform> platforms) {
        if (ladder == null) return false;

        float ladderBottom = ladder.getYPos() + ladder.getHeight();
        float playerBottom = player.getYPos() + player.getHeight();

        if (playerBottom >= ladderBottom - PLATFORM_TOLERANCE) {
            boolean platformAtBottom = isPlatformAtLadderBottom(ladder, platforms, player);

            if (platformAtBottom) {
                player.setYPos(ladderBottom - player.getHeight() - 1);
                return false;
            } else if (ladder.isCanPassThroughPlatform()) {
                return false;
            } else {
                player.setYPos(ladderBottom - player.getHeight() - 1);
                return true;
            }
        }
        return true;
    }

    private static boolean isPlatformAtLadderBottom(Ladder ladder, List<Platform> platforms, Player player) {
        float ladderBottom = ladder.getYPos() + ladder.getHeight();

        return platforms.stream().anyMatch(platform ->
                Math.abs(platform.getYPos() - ladderBottom) < PLATFORM_TOLERANCE &&
                        player.getXPos() + player.getWidth() > platform.getXPos() &&
                        player.getXPos() < platform.getXPos() + platform.getWidth()
        );
    }

    //Ladder Collision
    public static boolean isValidLadderContact(Player player, Ladder ladder) {
        Hitbox playerHitbox = player.getHitboxByName(HITBOX_LADDER);
        Hitbox ladderHitbox = ladder.getDefaultHitbox();

        if (playerHitbox == null || ladderHitbox == null || player.isJumping()) return false;

        return playerHitbox.intersects(ladderHitbox);
    }

    public static Ladder findBestLadder(Player player, List<Ladder> ladders, boolean movingUp) {
        return ladders.stream()
                .filter(ladder -> isValidLadderContact(player, ladder))
                .max((ladder1, ladder2) -> compareLadders(ladder1, ladder2, player, movingUp))
                .orElse(null);
    }

    private static int compareLadders(Ladder ladder1, Ladder ladder2, Player player, boolean movingUp) {
        float playerY = player.getYPos();

        if (movingUp) {
            // Para movimiento hacia arriba, compara la distancia disponible hacia arriba
            float distance1 = playerY - ladder1.getYPos();
            float distance2 = playerY - ladder2.getYPos();
            return Float.compare(distance1, distance2);
        } else {
            // Para movimiento hacia abajo, compara la distancia disponible hacia abajo
            float bottom1 = ladder1.getYPos() + ladder1.getHeight();
            float bottom2 = ladder2.getYPos() + ladder2.getHeight();
            float playerBottom = playerY + player.getHeight();
            float distance1 = bottom1 - playerBottom;
            float distance2 = bottom2 - playerBottom;
            return Float.compare(distance1, distance2);
        }
    }

    //Barrel collison
    public static CollisionResult handleBarrelCollision(Player player, List<Barrel> barrels) {
        Hitbox playerHitbox = player.getHitboxByName(HITBOX_BARREL);
        if (playerHitbox == null) return new CollisionResult(false, false);

        return barrels.stream()
                .map(barrel -> checkSingleBarrelCollision(player, barrel, (RectangleHitbox) playerHitbox))
                .filter(CollisionResult::hasCollision)
                .findFirst()
                .orElse(new CollisionResult(false, false));
    }

    public static void resetBarrelJumpStates(List<Barrel> barrels) {
        barrels.forEach(Barrel::resetJumpState);
    }

    private static CollisionResult checkSingleBarrelCollision(Player player, Barrel barrel, RectangleHitbox playerHitbox) {
        RectangleHitbox barrelHitbox = (RectangleHitbox) barrel.getDefaultHitbox();
        if (barrelHitbox == null) return new CollisionResult(false, false);

        BarrelCollisionData collisionData = new BarrelCollisionData(
                playerHitbox.getYPos() + playerHitbox.getHeight() <= barrelHitbox.getYPos() + BARREL_TOP_OFFSET,
                isXOverlapping(playerHitbox, barrelHitbox),
                Math.abs(barrelHitbox.getYPos() - (playerHitbox.getYPos() + playerHitbox.getHeight())) < BARREL_JUMP_HEIGHT
        );

        return evaluateBarrelCollision(player, barrel, playerHitbox, barrelHitbox, collisionData);
    }

    private static boolean isXOverlapping(RectangleHitbox a, RectangleHitbox b) {
        return a.getXPos() < b.getXPos() + b.getWidth() &&
                a.getXPos() + a.getWidth() > b.getXPos();
    }

    private static CollisionResult evaluateBarrelCollision(
            Player player,
            Barrel barrel,
            Hitbox playerHitbox,
            Hitbox barrelHitbox,
            BarrelCollisionData data) {

        if (player.getCurrentState() == PlayerState.JUMPING &&
                data.isAboveBarrel &&
                data.isXOverlapping &&
                data.isCloseEnough &&
                !barrel.hasBeenJumpedOver()) {

            return new CollisionResult(true, true);
        } else if (playerHitbox.intersects(barrelHitbox) && !data.isAboveBarrel) {
            return new CollisionResult(true, false);
        }

        return new CollisionResult(false, false);
    }

    //Victory collision
    public static boolean checkVictoryCondition(Player player, VictoryZone victoryPlatform) {
        Hitbox playerHitbox = player.getDefaultHitbox();
        Hitbox victoryHitbox = victoryPlatform.getDefaultHitbox();

        if (playerHitbox == null || victoryHitbox == null) return false;

        // Solo permitir victoria en estados específicos
        boolean isValidState = player.getCurrentState() == PlayerState.IDLE ||
                player.getCurrentState() == PlayerState.WALKING;

        return playerHitbox.intersects(victoryHitbox) && isValidState;
    }

    public record CollisionPoints(
            float centerX,
            float bottom,
            float previousBottom,
            float leftCheckX,
            float rightCheckX
    ) {
    }

    private record BarrelCollisionData(
            boolean isAboveBarrel,
            boolean isXOverlapping,
            boolean isCloseEnough
    ) {
    }

    public record CollisionResult(
            boolean hasCollision,
            boolean isJumpOver
    ) {
    }
}