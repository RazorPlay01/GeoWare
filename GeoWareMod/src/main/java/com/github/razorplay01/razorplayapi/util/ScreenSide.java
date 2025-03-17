package com.github.razorplay01.razorplayapi.util;

import com.github.razorplay01.razorplayapi.util.hitbox.RectangleHitbox;
import com.github.razorplay01.razorplayapi.util.screen.GameScreen;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScreenSide {
    TOP("top"),
    BOTTOM("bottom"),
    LEFT("left"),
    RIGHT("right");
    private final String side;

    public static void verifyScreenBoundsCollision(Entity entity, GameScreen gameScreen, RectangleHitbox hitbox) {
        int screenX = gameScreen.getGame().getScreen().getGameScreenXPos();
        int screenY = gameScreen.getGame().getScreen().getGameScreenYPos();
        int screenWidth = gameScreen.getGame().getScreenWidth();
        int screenHeight = gameScreen.getGame().getScreenHeight();

        if (entity.xPos < screenX) {
            entity.xPos = screenX;
            onScreenBoundaryCollision(ScreenSide.LEFT, entity);
            return;
        }

        if (entity.xPos + hitbox.getWidth() > screenX + screenWidth) {
            entity.xPos = screenX + screenWidth - hitbox.getWidth();
            onScreenBoundaryCollision(ScreenSide.RIGHT, entity);
            return;
        }

        if (entity.yPos < screenY) {
            entity.yPos = screenY;
            onScreenBoundaryCollision(ScreenSide.TOP, entity);
            return;
        }

        if (entity.yPos + hitbox.getHeight() > screenY + screenHeight) {
            entity.yPos = screenY + screenHeight - hitbox.getHeight();
            onScreenBoundaryCollision(ScreenSide.BOTTOM, entity);
        }
    }

    public static void onScreenBoundaryCollision(ScreenSide side, Entity entity) {
        if (side.equals(ScreenSide.LEFT) || side.equals(ScreenSide.RIGHT)) {
            entity.setVelocityX(0);
        } else if (side.equals(ScreenSide.TOP) || side.equals(ScreenSide.BOTTOM)) {
            entity.setVelocityY(0);
        }
    }
}